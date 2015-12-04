SUMMARY = "Script bin config files"
SECTION = "base"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b3597d12946881e13cb3b548d1173851"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SCRIPTBIN = "script.bin.OPI-PLUS"
RESOLUTIONS = "480p 720p50 720p60 1080p50 1080p60"
OUTPUTMETHOD = "hdmi dvi"

def scriptbin_prefix(str1, str2):
    out = ""
    for i in str1.split( ):
        for j in str2.split( ):
            out += j + i + " "
    return out

RESOLUTIONS_UNDERSCORE = "${@scriptbin_prefix('${RESOLUTIONS}', '_')}"
OUTPUTMETHOD_UNDERSCORE = "${@scriptbin_prefix('${OUTPUTMETHOD}', '_')}"
SCRIPTBIN_SUFFIX = "${@scriptbin_prefix('${OUTPUTMETHOD_UNDERSCORE}','${RESOLUTIONS_UNDERSCORE}')}"

SCRIPT_FILES = "${@scriptbin_prefix('${SCRIPTBIN_SUFFIX}','${SCRIPTBIN}')}"

SRC_URI = "file://LICENSE \
	${@scriptbin_prefix('${SCRIPT_FILES}', 'file://')}"

S = "${WORKDIR}"

FILES_${PN} = "/boot/${SCRIPTBIN}* /boot/script.bin"

SCRIPTBIN_SELECTED ?= "720p50_hdmi"

do_compile () {
	:
}

inherit deploy

do_install () {
	install -d ${D}/boot
	for file in ${SCRIPT_FILES}; do
		install $file ${D}/boot/$file
	done
	install ${SCRIPTBIN}_${SCRIPTBIN_SELECTED} ${D}/boot/script.bin
}

do_deploy () {
	install -d ${DEPLOYDIR}/bootfiles
	for file in ${SCRIPT_FILES}; do
		install $file ${DEPLOYDIR}/bootfiles/$file
	done
	install ${SCRIPTBIN}_${SCRIPTBIN_SELECTED} ${DEPLOYDIR}/bootfiles/script.bin
}

addtask deploy after do_compile before do_build
