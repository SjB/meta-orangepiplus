SUMMARY = "Plugins for certain ALSA sound card drivers"
HOMEPAGE = "http://www.alsa-project.org"
BUGTRACKER = "https://bugtrack.alsa-project.org/alsa-bug/login_page.php"
SECTION = "console/utils"
LICENSE = "GPLv2 & LGPLv2+"
DEPENDS = "alsa-lib"

LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

SRC_URI = "ftp://ftp.alsa-project.org/pub/plugins/${BP}.tar.bz2 "
#           file://autotools.patch \
#           ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '', \
#                                'file://makefile_no_gtk.patch', d)}"

SRC_URI[md5sum] = "6fcbbb31e96f8ebc5fb926184a717aa4"
SRC_URI[sha256sum] = "426f8af1a07ee9d8c06449524d1f0bd59a06e0331a51aa3d59d343a7c6d03120"

inherit autotools-brokensep pkgconfig

CLEANBROKEN = "1"

EXTRA_OEMAKE += "GITCOMPILE_ARGS='--host=${HOST_SYS} --build=${BUILD_SYS} --target=${TARGET_SYS} --with-libtool-sysroot=${STAGING_DIR_HOST} --prefix=${prefix}'"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulse', '', d)} \
	jack samplerate maemo avcodec"
PACKAGECONFIG[pulse] = "--enable-pulseaudio, --disable-pulseaudio,pulseaudio"
PACKAGECONFIG[jack] = "--enable-jack, --disable-jack, "
PACKAGECONFIG[samplerate] = "--enable-samplerate, --disable-samplerate, libsamplerate0"
PACKAGECONFIG[maemo] = "--enable-maemo-plugin --enable-maemo-resource-manager, --disable-maemo-plugin --disable-maemo-resource-manager, "
PACKAGECONFIG[avcodec] = "--enable-avcodec, --disable-avcodec, libav"

# configure.ac/.in doesn't exist so force copy
AUTOTOOLS_COPYACLOCAL = "1"

do_compile_prepend () {
    #Automake dir is not correctly detected in cross compilation case
    export AUTOMAKE_DIR="$(automake --print-libdir)"
    export ACLOCAL_FLAGS="--system-acdir=${ACLOCALDIR}/"
}

#PACKAGES += "${PN}-pulse"
#FILES_${PN} = ""
#FILES_${PN}-dbg = ""
#FILES_${PN}-doc = ""
#FILES_${PN}-pulse = "${libdir}/alsa-lib/libasound_module_*_pulse* ${datadir}/alsa"
#RDEPENDS_${PN}-pulse = "libpulse libsndfile1 libpulsecommon"

FILES_${PN} += "${datadir}/alsa ${libdir}/alsa-lib/libasound*"
FILES_${PN}-dbg += "${libdir}/alsa-lib/.debug"

INSANE_SKIP_${PN} = "dev-so"
