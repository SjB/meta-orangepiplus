
#
# Bitbake file to install the mali drivers
#

DESCRIPTION = "Sunxi Mali-400 support libraries."
SECTION = "graphics"
DEPENDS = "libump"
LICENSE = "MIT&Proprietary"
LIC_FILES_CHKSUM = "file://version/version.c;beginline=1;endline=22;md5=3331d9ebf9926606b171dc3e7811dbec"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_URI = "git://github.com/linux-sunxi/sunxi-mali.git;protocol=https;name=common"
SRC_URI += "git://github.com/linux-sunxi/sunxi-mali-proprietary.git;protocol=https;name=proprietary;destsuffix=git/lib/mali"

SRCREV_common = "d343311efc8db166d8371b28494f0f27b6a58724"
SRCREV_proprietary = "1c5063f43cdc9de341c0d63b2e3921cab86c7742"

S = "${WORKDIR}/git"

#EXTRA_OECONF = ""
#EXTRA_OEMAKE = ""

OVERRIDES .= "${@bb.utils.contains("TUNE_FEATURES", "vfpv4", ":vfpv4", "", d)}"

ABI_vfpv4 ?= "armhf"
ABI ??= "armel"

MALI_VERSION ??= "r3p2-01rel1"

PACKAGECONFIG ??= "${@base_contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
		${@base_contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)}"
PACKAGECONFIG[wayland] = "EGL_TYPE=framebuffer,,,"
PACKAGECONFIG[x11] = "EGL_TYPE=x11,,virtual/libx11 libxau libxdmcp libdri2,"

EGL_TYPE ??= "framebuffer"

do_compile() {
	cd ${S}
	oe_runmake config ABI=${ABI} VERSION=${MALI_VERSION} EGL_TYPE=${EGL_TYPE}
	oe_runmake
}

do_install() {
	cd ${S}
	unset libdir
	unset includedir
	unset prefix
	install -d ${D}/usr/lib/
	oe_runmake install DESTDIR=${D}
}

split_and_strip_files() {
	:
}
