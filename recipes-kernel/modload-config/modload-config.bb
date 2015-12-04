SUMMARY = "Config for loading of modules"
SECTION = "base"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b3597d12946881e13cb3b548d1173851"

SRC_URI = "file://LICENSE \
	file://wlan.conf"

S = "${WORKDIR}"

do_compile () {
	:
}

do_install () {
	install -d ${D}${sysconfdir}/modules-load.d/
	for i in ${SRC_URI}
	do
		uri=$(echo $i | cut -d':' -f 1 - )
		filename=$(echo $i | cut -d':' -f 2 - | cut -c 3- -)
		ext=$(echo $filename | cut -d'.' -f 2 -)
		if [ "$ext" = "conf" -a "$uri" = "file" ]; then
			echo installing $filename
			install -m 0644 ${WORKDIR}/$filename ${D}${sysconfdir}/modules-load.d/$filename
		fi
	done
}
