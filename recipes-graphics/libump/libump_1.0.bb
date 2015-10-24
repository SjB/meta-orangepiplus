#
# Bitbake file to make the libump library
#

DESCRIPTION = "Unified Memory Provider userspace API source code needed for xf86-video-mali compilation "
SECTION = "graphics"
DEPENDS = ""
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/ump_ref_drv.c;beginline=1;endline=15;md5=eb85dbb0a2ab5bb16d74133c23cdaec5"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRCREV = "ec0680628744f30b8fac35e41a7bd8e23e59c39f"
SRC_URI = "git://github.com/linux-sunxi/libump.git;protocol=https"

S = "${WORKDIR}/git"

#EXTRA_OECONF = ""
#EXTRA_OEMAKE = ""

inherit autotools
