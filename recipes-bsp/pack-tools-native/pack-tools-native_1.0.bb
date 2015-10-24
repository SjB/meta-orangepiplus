SUMMARY = "Tools to create the image"
SECTION = "tools"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://tools/pack;md5=123c29abaa655cd6811dad6d7d8cea9a"

SRC_URI = "file://pack-tools_1.0.tar.gz;subdir=tools"

S = "${WORKDIR}"

PACKTOOL_DIR = "${datadir}/packtools/"

#INSANE_SKIP_${PN} += "already-stripped"

do_install() {
	install -d ${D}${PACKTOOL_DIR}
	cp -r ${S}/tools/* ${D}${PACKTOOL_DIR}
}

inherit native
