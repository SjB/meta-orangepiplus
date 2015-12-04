
#
# Bitbake file to install the mali drivers
#

DESCRIPTION = "Sunxi Mali-400 support libraries."
SECTION = "graphics"
DEPENDS = "libump \
	${@base_contains('DISTRO_FEATURES', 'x11', 'libxdmcp libx11 libdrm libxcb libxext libxau libxfixes', '', d)}"
LICENSE = "MIT&Proprietary"
LIC_FILES_CHKSUM = "file://version/version.c;beginline=1;endline=22;md5=3331d9ebf9926606b171dc3e7811dbec"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_URI = "git://github.com/robojan/sunxi-mali.git;protocol=https;name=common"
SRC_URI += "git://github.com/robojan/sunxi-mali-proprietary.git;protocol=https;name=proprietary;destsuffix=git/lib/mali"

SRCREV_common = "c70e7b1b4234974b970d46c5db73afd6ef2b1962"
SRCREV_proprietary = "2dd5318f0869a1a289a94ee2e8a41533796e4a07"

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

EGL_TYPE ??= "${@base_contains('DISTRO_FEATURES', 'x11', 'x11', 'framebuffer', d)}"

do_compile() {
	cd ${S}
	oe_runmake config ABI=${ABI} VERSION=${MALI_VERSION} EGL_TYPE=${EGL_TYPE}
	oe_runmake
}

export DESTDIR = "${D}"
export prefix = "/usr"
export libdir = "/usr/lib/mali"
export includedir = "/usr/include/mali"

FILES_${PN} = "${libdir}/*"
FILES_${PN}-dev = "${includedir}/*"

INSANE_SKIP_${PN} += "dev-so"

do_install() {
	cd ${S}
	export prefix=$prefix/
	export libdir=${D}$libdir/
	export includedir=${D}$includedir/
	install -d $libdir
	oe_runmake install DESTDIR=${D} 
}

split_and_strip_files() {
	:
}
