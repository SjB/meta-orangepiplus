SUMMARY = "Universal Boot Loader for embedded devices - Lichee version"
HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
SECTION = "bootloaders"
PROVIDES = "virtual/bootloader"

DEPENDS = "pack-tools-native dos2unix-native"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit uboot-config deploy

FILESEXTRAPATHS_prepend :=  "${THISDIR}/${PN}:${THISDIR}/../files:"


SRC_URI = "file://u-boot-2011.09-lichee.tar.gz;md5=f0221bbcdff4bbdf60190ffb56e106a1;sha256=05cc0ff84bde50fcd16e7680878fd1dfdf940decde6c29edf33df9ca23767821"
SRC_URI += "file://0001-u-boot-orangepiplus.patch;patchdir=u-boot-2011.09 \
	file://${MACHINE}.fex;subdir=u-boot-2011.09/"
	

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://u-boot-2011.09/COPYING;md5=1707d6db1d42237583f50183a5651ecb"

OVERRIDES .= "${@bb.utils.contains("TUNE_FEATURES", "vfpv4", ":vfpv4", "", d)}"

SRC_URI_append_vfpv4 = " file://0003-neon-vfpv4-float.patch;patchdir=u-boot-2011.09"

S = "${WORKDIR}"

EXTRA_OEMAKE = 'CROSS_COMPILE=${TARGET_PREFIX} CC="${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS}" V=1'

# Allow setting an additional version string that will be picked up by the
# u-boot build system and appended to the u-boot version.  If the .scmversion
# file already exists it will not be overwritten.
UBOOT_LOCALVERSION ?= ""

# Some versions of u-boot use .bin and others use .img.  By default use .bin
# but enable individual recipes to change this value.
UBOOT_SUFFIX ??= "bin"
UBOOT_IMAGE ?= "u-boot-${MACHINE}-${PV}-${PR}.${UBOOT_SUFFIX}"
UBOOT_BINARY ?= "u-boot.${UBOOT_SUFFIX}"
UBOOT_SYMLINK ?= "u-boot-${MACHINE}.${UBOOT_SUFFIX}"
UBOOT_MAKE_TARGET ?= "all"

# Some versions of u-boot build an SPL (Second Program Loader) image that
# should be packaged along with the u-boot binary as well as placed in the
# deploy directory.  For those versions they can set the following variables
# to allow packaging the SPL.
SPL_BINARY ?= ""
SPL_IMAGE ?= "${SPL_BINARY}-${MACHINE}-${PV}-${PR}"
SPL_SYMLINK ?= "${SPL_BINARY}-${MACHINE}"

# Additional environment variables or a script can be installed alongside
# u-boot to be used automatically on boot.  This file, typically 'uEnv.txt'
# or 'boot.scr', should be packaged along with u-boot as well as placed in the
# deploy directory.  Machine configurations needing one of these files should
# include it in the SRC_URI and set the UBOOT_ENV parameter.
UBOOT_ENV_SUFFIX ?= "txt"
UBOOT_ENV ?= ""
UBOOT_ENV_BINARY ?= "${UBOOT_ENV}.${UBOOT_ENV_SUFFIX}"
UBOOT_ENV_IMAGE ?= "${UBOOT_ENV}-${MACHINE}-${PV}-${PR}.${UBOOT_ENV_SUFFIX}"
UBOOT_ENV_SYMLINK ?= "${UBOOT_ENV}-${MACHINE}.${UBOOT_ENV_SUFFIX}"

do_compile() {
	cd u-boot-2011.09
	
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', 'ld-is-gold', '', d)}" = "ld-is-gold" ] ; then
		sed -i 's/$(CROSS_COMPILE)ld$/$(CROSS_COMPILE)ld.bfd/g' config.mk
	fi

	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS
	

	if [ ! -e ${B}/.scmversion -a ! -e ${S}/.scmversion ]
	then
		echo ${UBOOT_LOCALVERSION} > ${B}/.scmversion
		echo ${UBOOT_LOCALVERSION} > ${S}/.scmversion
	fi
    
    if [ "x${UBOOT_CONFIG}" != "x" ]
    then
        for config in ${UBOOT_MACHINE}; do
            i=`expr $i + 1`;
            for type  in ${UBOOT_CONFIG}; do
                j=`expr $j + 1`;
                if [ $j -eq $i ]
                then
                    oe_runmake O=${config} ${config}
                    oe_runmake O=${config} ${UBOOT_MAKE_TARGET}
                    cp  ${S}/${config}/${UBOOT_BINARY}  ${S}/${config}/u-boot-${type}.${UBOOT_SUFFIX}
                fi
            done
            unset  j
        done
        unset  i
    else
        oe_runmake ${UBOOT_MACHINE}
        oe_runmake ${UBOOT_MAKE_TARGET}
    fi

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

FILES_${PN} = "/boot ${sysconfdir}"
