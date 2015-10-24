
#
# Bitbake file to compile the Mali-400 kernel drivers.
#

DESCRIPTION = "Mali-400 kernel drivers for external compilation for use with linux-sunxi kernel. "
SECTION = "drivers"
DEPENDS = "linux-yocto"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://driver/src/devicedrv/mali/linux/mali_kernel_linux.c;beginline=1;endline=9;md5=893673a50bcf4c5f2afa32c64e9577e6"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_URI = "git://github.com/linux-sunxi/mali-400-kernel-drivers.git;protocol=https;branch=${PV}"
SRC_URI += "file://Makefile;subdir=git"
SRC_URI += "file://0001-ump-make-compilable.patch \
		file://0002-remove-clean-mali-from-ump.patch \
		file://0003-mali-make-compilable.patch \
		file://0004-fix-strict-strtoxx-funcs.patch \
		file://0005-changed-shrinker-implementation.patch \
		file://0006-update-timekeeping.patch \
		file://0007-fix-drm-include.patch \
		file://0008-replace-drm-memory-management.patch"

SRCREV = "0bd92ce52c1259f3533515c34540660553fb895f"

S = "${WORKDIR}/git"

OVERRIDES .= "${@bb.utils.contains("TUNE_FEATURES", "vfpv4", ":vfpv4", "", d)}"

UMP_CONFIG ?= "pb-virtex5"
MALI_CONFIG ?= "pb-virtex5-m400-2"
MALI_TARGET_PLATFORM ?= "default"
MALI_TARGETS ?= "mali ump"

EXTRA_OECONF += "MALI_TARGETS=${MALI_TARGETS}"
EXTRA_OEMAKE += 'UMP_CONFIG=${UMP_CONFIG} MALI_CONFIG=${MALI_CONFIG} MALI_PLATFORM=${MALI_TARGET_PLATFORM} MALI_TARGETS="${MALI_TARGETS}"'

inherit module
