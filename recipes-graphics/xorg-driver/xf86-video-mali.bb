require xorg-driver-video.inc

SUMMARY = "X.Org X server -- fbturbo display driver"

DEPENDS += "libump virtual/kernel"
RDEPENDS_${PN} += "libUMP.so"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README.txt;beginline=1;endline=21;md5=774fedaa798d52305e159f9a437515cc"

SRC_URI = "file://DX910-SW-99003-r4p0-00rel0.tgz \
	http://cgit.freedesktop.org/~cooperyuan/compat-api/plain/compat-api.h;name=compat-api \
	file://fixMakefile.patch"

SRC_URI[compat-api.md5sum] = "3e33ea9b73ac01125ce6c8b29efc8c42"
SRC_URI[compat-api.sha256sum] = "b986a093b938b3b20d5893b37c81994102e77be5a6f26dd706cd6db6a25851aa"

PATCHTOOL = "patch"

S = "${WORKDIR}/DX910-SW-99003-r4p0-00rel0/x11/xf86-video-mali-0.0.1/"

do_modify_src() {
    cp -f ${WORKDIR}/compat-api.h ${S}/src/compat-api.h
}

addtask do_modify_src after do_unpack before do_configure

EXTRA_OECONF = 'CPPFLAGS="-I${STAGING_KERNEL_DIR}/modules/mali/DX910-SW-99002-r4p0-00rel0_modify/driver/src/devicedrv -DHAVE_STRNDUP"'

FILES_${PN} = "${libdir}"

inherit autotools pkgconfig

