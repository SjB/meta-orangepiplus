SUMMARY = "Generates the boot0 and u-boot image with correct system info"
SECTION = "bootloaders"
PROVIDES = "virtual/bootloader"
DEPENDS = "pack-tools-native dos2unix-native"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"
FILESEXTRAPATHS_prepend := "${THISDIR}/../files:"

inherit deploy

PACKTOOL_DIR ?= "${STAGING_DATADIR_NATIVE}/packtools/"

BOOT0_BIN_FILE ?= "${PACKTOOL_DIR}/chips/${CHIP_NAME}/bin/${BOOT0_IMAGE}"
UBOOT_BIN_FILE ?= "${PACKTOOL_DIR}/chips/${CHIP_NAME}/bin/u-boot-${CHIP_NAME}.bin"

SRC_URI = "file://${MACHINE}.fex;subdir=work \
	file://${BOOT0_BIN_FILE};subdir=work \
	file://${UBOOT_BIN_FILE};subdir=work"

S = "${WORKDIR}/work"

do_fetch[dirs] += "${S}"

do_compile_system_config() {
	# Copy system config 
	cp -fv ${MACHINE}.fex ${B}/sys_config.fex

	# Fex file compiler requires fex file in CRLF format
	unix2dos ${B}/sys_config.fex

	# Compile fex file
	${PACKTOOL_DIR}pctools/linux/mod_update/script sys_config.fex
}

do_compile_boot0() {

	# Copy boot0
	cp -fv ${BOOT0_BIN_FILE} ${B}/boot0.fex

	# Patch boot0
	if [ "x${BOOT0_TYPE}" = "xsdcard" ]; then
		${PACKTOOL_DIR}pctools/linux/mod_update/update_boot0 boot0.fex sys_config.bin SDMMC_CARD
	elif [ "x${BOOT0_TYPE}" = "xemmc" ]; then
		${PACKTOOL_DIR}pctools/linux/mod_update/update_boot0 boot0.fex sys_config.bin NAND_CARD
	else
		echo No valid BOOT0 type selected 1>&2
		exit 1
	fi
	cp -fv boot0.fex boot0.${BOOT0_SUFFIX}
}

do_compile_uboot() {
	# Copy uboot
	cp -fv ${UBOOT_BIN_FILE} ${B}/uboot.fex

	# Patch uboot
	${PACKTOOL_DIR}pctools/linux/mod_update/update_uboot uboot.fex sys_config.bin
	cp -fv uboot.fex u-boot.bin
}

do_compile() {
	do_compile_system_config
	do_compile_boot0
	do_compile_uboot
}


do_install() {
	install -d ${D}/boot
	install ${S}/boot0.${BOOT0_SUFFIX} ${D}/boot/${BOOT0_IMAGE}
	install ${S}/u-boot.bin ${D}/boot/u-boot.${UBOOT_SUFFIX}
	cd ${D}/boot/
	rm -f boot0.${BOOT0_SUFFIX} 
	ln -s ${BOOT0_IMAGE} boot0.${BOOT0_SUFFIX}
}

FILES_${PN} = "/boot"

do_deploy() {
	install -d ${DEPLOYDIR}
	install ${S}/boot0.${BOOT0_SUFFIX} ${DEPLOYDIR}/${BOOT0_IMAGE}
	install ${S}/u-boot.bin ${DEPLOYDIR}/u-boot.${UBOOT_SUFFIX}
	cd ${DEPLOYDIR}
	rm -f boot0.${BOOT0_SUFFIX}
	ln -s ${BOOT0_IMAGE} boot0.${BOOT0_SUFFIX}	
}

addtask deploy before do_build after do_compile
