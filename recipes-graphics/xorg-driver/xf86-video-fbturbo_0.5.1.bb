require xorg-driver-video.inc

SUMMARY = "X.Org X server -- fbturbo display driver"
DESCRIPTION = "fbturbo is an Xorg video driver, primarily optimized for the devices powered by the Allwinner SoC (A10, A13, A20). It can use some of the 2D/3D hardware acceleration features."

DEPENDS += "libump"
RDEPENDS_${PN} += "libUMP.so"
LICENSE = "XFree86-1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=f91dc3ee5ce59eb4b528e67e98a31266"

#branch r3p2
#SRCREV = "6ec85e995e855ab537df905e3717ba3c7935e108" 
#branch master
SRCREV = "f9a6ed78419f0b98cf2c3ce3cdd4c97fe9a46195"
SRC_URI = "git://github.com/ssvb/xf86-video-fbturbo.git;protocol=https"
S = "${WORKDIR}/git"

FILES_${PN} = "${libdir}"

inherit autotools pkgconfig
