#
# Bitbake file to make the libump library
#

DESCRIPTION = "Unified Memory Provider userspace API source code needed for xf86-video-mali compilation "
SECTION = "graphics"
DEPENDS = "util-macros"
LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://COPYING;md5=827da9afab1f727f2a66574629e0f39c"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRCREV = "4f1eef3183df2b270c3d5cbef07343ee5127a6a4"
SRC_URI = "git://github.com/robclark/libdri2.git;protocol=https"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
