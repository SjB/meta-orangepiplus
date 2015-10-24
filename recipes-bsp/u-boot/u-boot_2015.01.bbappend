PROVIDES = ""
DEPENDS = "pack-tools-native dos2unix-native"

SRCREV = "e5ceeca211883d9f6b25f84e0e3c5fe2afaaf350"
SRC_URI = "git://github.com/allwinner-zh/bootloader.git;protocol=https"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=1707d6db1d42237583f50183a5651ecb"
S .= "/u-boot-2011.09"

PACKTOOL_DIR ?= "${STAGING_DATADIR_NATIVE}/packtools/"

#patches
FILESEXTRAPATHS_prepend :=  "${THISDIR}/patches:${THISDIR}/../files:"
SRC_URI += "file://0001-orangepi_bugs_fixed.patch \
	file://0002-no_copying_external.patch \
	file://${MACHINE}.fex;subdir=git/u-boot-2011.09/"

OVERRIDES .= "${@bb.utils.contains("TUNE_FEATURES", "vfpv4", ":vfpv4", "", d)}"

SRC_URI_append_vfpv4 = " file://0003-neon-vfpv4-float.patch"

do_patch_config() {
	# Copy system config 
	cp -fv ${MACHINE}.fex ${B}/sys_config.fex

	# Fex file compiler requires fex file in CRLF format
	unix2dos ${B}/sys_config.fex

	# Compile fex file
	${PACKTOOL_DIR}pctools/linux/mod_update/script sys_config.fex

	if [ "x${UBOOT_CONFIG}" != "x" ]
	then
		for config in ${UBOOT_MACHINE}; do
			i=`expr $i + 1`;
			for type in ${UBOOT_CONFIG}; do
				j=`expr $j + 1`;
				if [ $j -eq $i ]
				then
					# Rename compile output
					mv ${S}/${config}/u-boot-${type}.${UBOOT_SUFFIX} u-boot.fex

					# Patch u-boot
					${PACKTOOL_DIR}pctools/linux/mod_update/update_uboot u-boot.fex sys_config.bin

					# Rename u-boot back
					mv u-boot.fex ${S}/${config}/u-boot-${type}.${UBOOT_SUFFIX}
				fi
			done
			unset  j
		done
		unset  i
	else
		# Rename compile output
		mv ${UBOOT_BINARY} u-boot.fex

		# Patch u-boot
		${PACKTOOL_DIR}pctools/linux/mod_update/update_uboot u-boot.fex sys_config.bin

		# Rename u-boot back
		mv u-boot.fex ${UBOOT_BINARY}
	fi
	
}

addtask do_patch_config after do_compile before do_install before do_deploy
