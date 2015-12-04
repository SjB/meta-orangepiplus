DESCRIPTION = "Experimental VDPAU for Allwinner sunxi SoCs (WiP)"
SECTION = "graphics"
DEPENDS = "libvdpau"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://h264.c;beginline=1;endline=18;md5=d272116b63ed2796aab264c467da62b1"

SRC_URI = "git://github.com/linux-sunxi/libvdpau-sunxi.git;protocol=https"
SRCREV = "c5ce48301d88f457da85ce212bd49a78db7580d3"

S = "${WORKDIR}/git"

FILES_${PN} += "${libdir}/vdpau/lib*.so.*"
FILES_${PN}-dev += "${libdir}/vdpau/lib*.so ${libdir}/vdpau/*.la"
FILES_${PN}-dbg += "${libdir}/vdpau/.debug"

do_install() {
	oe_runmake DESTDIR=${D} install
}

pkg_postinst_${PN}() {
#!/bin/sh -e
echo 'export VDPAU_DRIVER=sunxi' >> /etc/profile
}
