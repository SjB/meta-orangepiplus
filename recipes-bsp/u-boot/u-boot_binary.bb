SUMMARY = "Universal Boot Loader for embedded devices - Binary image"
HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
SECTION = "bootloaders"
PROVIDES = "virtual/bootloader"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit uboot-config deploy

FILESEXTRAPATHS_prepend :=  "${THISDIR}/${PN}:${THISDIR}/../files:"


SRC_URI = "file://uboot.bin;md5=aea6a3caa42012e4efa5dc99d20e0b02;sha256=9a1024b4e7e50b9d97d6051a1e25496f0236db73048278f8969b49529d1180c1 \
	file://uEnv.txt"
	

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://uboot.bin;md5=58e42cf2c641e22119e4ec52b36eac30"

S = "${WORKDIR}"

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

FILES_${PN} = "/boot ${sysconfdir}"

do_install() {
	install -d ${D}/boot
	install ${S}/uboot.bin ${D}/boot/${UBOOT_IMAGE}
	cd ${D}/boot
	ln -sf ${UBOOT_IMAGE} ${D}/boot/${UBOOT_BINARY}
}

do_deploy() {
	install -d ${DEPLOYDIR}
	install ${S}/uboot.bin ${DEPLOYDIR}/${UBOOT_IMAGE}
	cd ${DEPLOYDIR}
	rm -f ${UBOOT_BINARY} ${UBOOT_SYMLINK}
	ln -sf ${UBOOT_IMAGE} ${UBOOT_SYMLINK}
	ln -sf ${UBOOT_IMAGE} ${UBOOT_BINARY}
	
	if [ "x${UBOOT_ENV}" != "x" ]; then
		echo Deploying ${UBOOT_ENV_BINARY}
		install ${S}/${UBOOT_ENV_BINARY} ${DEPLOYDIR}/${UBOOT_ENV_IMAGE}
		rm -f ${UBOOT_ENV_BINARY} ${UBOOT_ENV_SYMLINK}
		ln -sf ${UBOOT_ENV_IMAGE} ${UBOOT_ENV_BINARY}
		ln -sf ${UBOOT_ENV_IMAGE} ${UBOOT_ENV_SYMLINK}
	fi
}

addtask deploy before do_build after do_compile
