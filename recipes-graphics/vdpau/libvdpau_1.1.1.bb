DESCRIPTION = "Video Decode and Presentation API for Unix"
SECTION = "graphics"
DEPENDS = "libxdmcp libx11 libxcb libxext libxau"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=83af8811a28727a13f04132cc33b7f58"

SRC_URI = "http://people.freedesktop.org/~aplattner/vdpau/libvdpau-1.1.1.tar.gz"
SRC_URI[md5sum] = "ac8b21012035c04fd1ec8a9ae6934264"
SRC_URI[sha256sum] = "5fe093302432ef05086ca2ee429c789b7bf843e166d482d166e56859b08bef55"

S = "${WORKDIR}/${PN}-${PV}"

FILES_${PN} += "${libdir}/vdpau/lib*.so.*"
FILES_${PN}-dev += "${libdir}/vdpau/lib*.so ${libdir}/vdpau/*.la"
FILES_${PN}-dbg += "${libdir}/vdpau/.debug"

inherit autotools pkgconfig
