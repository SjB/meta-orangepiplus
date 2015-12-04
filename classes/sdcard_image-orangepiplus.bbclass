inherit image_types
#
# Create an image that can by written onto a SD card using dd.
#

# This image depends on the rootfs image
IMAGE_TYPEDEP_allwinner-sdcard = "${SDIMG_ROOTFS_TYPE}"

# Set kernel and boot loader
IMAGE_BOOTLOADER ?= "boot0 u-boot"

# Set initramfs extension
KERNEL_INITRAMFS ?= ""

# Boot partition volume id
BOOTDD_VOLUME_ID ?= "${MACHINE} Boot"

# Boot partition size [in KiB] (will be rounded up to IMAGE_ROOTFS_ALIGNMENT)
BOOT_SPACE ?= "40960"

# Set alignment to 4MB [in KiB]
IMAGE_ROOTFS_ALIGNMENT = "20480"

# Use an uncompressed ext3 by default as rootfs
SDIMG_ROOTFS_TYPE ?= "ext4"
SDIMG_ROOTFS = "${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.${SDIMG_ROOTFS_TYPE}"

IMAGE_DEPENDS_allwinner-sdcard = " \
			parted-native \
			mtools-native \
			dosfstools-native \
			virtual/kernel \
			${IMAGE_BOOTLOADER} \
			"

# SD card image name
SDIMG = "${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.img"

# Compression method to apply to SDIMG after it has been created. Supported
# compression formats are "gzip", "bzip2" or "xz". The original .rpi-sdimg file
# is kept and a new compressed file is created if one of these compression
# formats is chosen. If SDIMG_COMPRESSION is set to any other value it is
# silently ignored.
#SDIMG_COMPRESSION ?= ""

# Additional files and/or directories to be copied into the vfat partition from the IMAGE_ROOTFS.
FATPAYLOAD ?= ""

#IMAGEDATESTAMP = "${@time.strftime('%Y.%m.%d',time.gmtime())}"

UBOOT_ENV_BINARY ?= "${UBOOT_ENV}.${UBOOT_ENV_SUFFIX}"

IMAGE_CMD_allwinner-sdcard = "create_image_allwinner_sdcard"

create_image_allwinner_sdcard() {
	# Align partitions
	BOOT_SPACE_ALIGNED=$(expr ${BOOT_SPACE} + ${IMAGE_ROOTFS_ALIGNMENT} - 1)
	BOOT_SPACE_ALIGNED=$(expr ${BOOT_SPACE_ALIGNED} - ${BOOT_SPACE_ALIGNED} % ${IMAGE_ROOTFS_ALIGNMENT})
	SDIMG_SIZE=$(expr ${IMAGE_ROOTFS_ALIGNMENT} + ${BOOT_SPACE_ALIGNED} + $ROOTFS_SIZE)

	echo "Creating filesystem with Boot partition ${BOOT_SPACE_ALIGNED} KiB and RootFS $ROOTFS_SIZE KiB"

	# Check if we are building with device tree support
	DTS="${KERNEL_DEVICETREE}"

	# Initialize sdcard image file
	echo "Creating empty image"
	rm -f ${SDIMG}
	dd if=/dev/zero of=${SDIMG} bs=1024 count=0 seek=${SDIMG_SIZE}

	echo "Create partitions"
	# Create partition table
	parted -s ${SDIMG} mklabel msdos
	# Create raw partition for boot0 and u-boot
	# parted -s ${SDIMG} unit KiB mkpart primary 1 ${IMAGE_ROOTFS_ALIGNMENT}
	# Create boot partition and mark it as bootable
	parted -s ${SDIMG} unit KiB mkpart primary fat32 ${IMAGE_ROOTFS_ALIGNMENT} $(expr ${BOOT_SPACE_ALIGNED} \+ ${IMAGE_ROOTFS_ALIGNMENT})
	parted -s ${SDIMG} unit KiB set 1 boot on
	# Create rootfs partition to the end of disk
	parted -s ${SDIMG} -- unit KiB mkpart primary ext2 $(expr ${BOOT_SPACE_ALIGNED} \+ ${IMAGE_ROOTFS_ALIGNMENT}) -1s
	parted ${SDIMG} print

	echo "Copying boot0 to image"
	dd if=${DEPLOY_DIR_IMAGE}/${BOOT0_IMAGE} of=${SDIMG} conv=notrunc seek=8 bs=1024
	echo "Copying u-boot to image"
	dd if=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} of=${SDIMG} conv=notrunc seek=16400 bs=1024

	echo "Create vfat image with boot files"
	# Create a vfat image with boot files
	BOOT_BLOCKS=$(LC_ALL=C parted -s ${SDIMG} unit b print | awk '/ boot/ { print substr($4, 1, length($4 -1)) / 512 /2 }')
	echo "Boot blocks: $BOOT_BLOCKS"
	echo "Creating partition"
	rm -f ${WORKDIR}/boot.img
	mkfs.vfat -n "${BOOTDD_VOLUME_ID}" -S 512 -C ${WORKDIR}/boot.img $BOOT_BLOCKS 
	echo "Installing boot files"
	mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/bootfiles/* ::/
	case "${KERNEL_IMAGETYPE}" in
	"uImage")
		echo "Installing boot0"
		mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/boot0.${BOOT0_SUFFIX} ::boot0.${BOOT0_SUFFIX}
		echo "Installing u-boot"
		mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} ::${SDIMG_KERNELIMAGE}
		if [ "x${UBOOT_ENV}" != "x" ]; then
			echo "Installing ${UBOOT_ENV_BINARY}"
			mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/${UBOOT_ENV_BINARY} ::${UBOOT_ENV_BINARY}
		fi
		echo installing uImage
		mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}${KERNEL_INITRAMFS}-${MACHINE}.bin ::uImage
		;;
	*)
		echo Kernel images other than uImage not supported
		exit 1;
		mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}${KERNEL_INITRAMFS}-${MACHINE}.bin ::${SDIMG_KERNELIMAGE}
		;;
	esac

	if [ -n ${FATPAYLOAD} ] ; then
		echo "Copying payload into VFAT"
		for entry in ${FATPAYLOAD} ; do
				# add the || true to stop aborting on vfat issues like not supporting .~lock files
				mcopy -i ${WORKDIR}/boot.img -s -v ${IMAGE_ROOTFS}$entry :: || true
		done
	fi

	# Add stamp file
	echo "${IMAGE_NAME}-${IMAGEDATESTAMP}" > ${WORKDIR}/image-version-info
	mcopy -i ${WORKDIR}/boot.img -v ${WORKDIR}//image-version-info ::

	# Burn Partitions
	dd if=${WORKDIR}/boot.img of=${SDIMG} conv=notrunc seek=1 bs=$(expr ${IMAGE_ROOTFS_ALIGNMENT} \* 1024) && sync && sync
	# If SDIMG_ROOTFS_TYPE is a .xz file use xzcat
	if echo "${SDIMG_ROOTFS_TYPE}" | egrep -q "*\.xz"
	then
		xzcat ${SDIMG_ROOTFS} | dd of=${SDIMG} conv=notrunc seek=1 bs=$(expr 1024 \* ${BOOT_SPACE_ALIGNED} + ${IMAGE_ROOTFS_ALIGNMENT} \* 1024) && sync && sync
	else
		dd if=${SDIMG_ROOTFS} of=${SDIMG} conv=notrunc seek=1 bs=$(expr 1024 \* ${BOOT_SPACE_ALIGNED} + ${IMAGE_ROOTFS_ALIGNMENT} \* 1024) && sync && sync
	fi

	# Optionally apply compression
	case "${SDIMG_COMPRESSION}" in
	"gzip")
		gzip -k9 "${SDIMG}"
		;;
	"bzip2")
		bzip2 -k9 "${SDIMG}"
		;;
	"xz")
		xz -k "${SDIMG}"
		;;
	esac
}
