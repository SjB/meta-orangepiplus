

FILESEXTRAPATHS_prepend :=  "${THISDIR}/files:"
SRC_URI += "file://linux-softcursor.patch \
	file://enablecursor.sh"

do_install_append () {
	install -d ${D}${sysconfdir}/profile.d
	install --mode=0755 ${WORKDIR}/enablecursor.sh ${D}${sysconfdir}/profile.d/enablecursor.sh
}

FILES_${PN} += "${sysconfdir}/profile.d/"
