#
# Bitbake file to make the Dos2Unix tool
#

DESCRIPTION = "Dos2Unix / Unix2Dos - Text file format converters"
SECTION = "support"
DEPENDS = ""
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://dos2unix-${PV}/COPYING.txt;md5=2f28f20fb0edd8d964c85726c6e80948"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_URI = "http://waterlan.home.xs4all.nl/dos2unix/dos2unix-7.3.1.tar.gz"
SRC_URI[md5sum] = "943b1765e14bd42848c62d84399855ab"
SRC_URI[sha256sum] = "f4d5df24d181c2efecf7631aab6e894489012396092cf206829f1f9a98556b94"

S = "${WORKDIR}"

do_install() {
	cd dos2unix-${PV}
	oe_runmake install DESTDIR=${D}
}

#FILES_${PN} = "/usr/bin/*"

BBCLASSEXTEND = "native"
