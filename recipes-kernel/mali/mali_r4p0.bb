
#
# Bitbake file to compile the Mali-400 kernel drivers.
#

DESCRIPTION = "Mali-400 kernel drivers for external compilation for use with linux-sunxi kernel. "
SECTION = "drivers"
DEPENDS = "virtual/kernel"
PROVIDES = "virtual/mali libump libmali"
LICENSE = "GPLv2&Apache-2.0"
LIC_FILES_CHKSUM = "file://DX910-SW-99002-r4p0-00rel0_modify/driver/src/devicedrv/mali/linux/mali_kernel_linux.c;beginline=1;endline=9;md5=7a6e4d35599c2043cf0d8c2a15cc7cc5 \
	file://DX910-SW-99006-r4p0-00rel0/driver/src/ump/readme.txt;beginline=1;endline=15;md5=b67f3604f0a4f7643e728cb817b7989b"
PACKAGE_ARCH = "${MACHINE_ARCH}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_URI = "file://mali-ddk-r4p0.tar.gz"

SRCREV = ""

S = "${WORKDIR}/mali-ddk-r4p0"

OVERRIDES .= "${@bb.utils.contains("TUNE_FEATURES", "vfpv4", ":vfpv4", "", d)}"

EXTRA_OECONF = ""
EXTRA_OEMAKE = ""

export VERBOSE = "1"

PACKAGES += "libump libump-dev libump-dbg libump-staticdev libmali libmali-dev libmali-dbg"
FILES_mali-dev = ""
FILES_mali-dbg = ""
FILES_libump = "${libdir}/libUMP.s*"
FILES_libump-dev = "${includedir}/ump"
FILES_libump-staticdev = "${libdir}/libUMP.a"
FILES_libump-dbg = "${libdir}/.debug/libUMP*"
FILES_libmali = "${libdir}/libEGL.s* ${libdir}/libGLESv1_CM.s* ${libdir}/libGLESv2.s* ${libdir}/libMali.s*"
FILES_libmali-dev = "${includedir}/khronos"
FILES_libmali-dbg = "${libdir}/.debug/libMali*"

inherit module

do_unpack_subarchives () {
	tar -zxf DX910-SW-99002-r4p0-00rel0_modify.tar.gz
	tar -zxf DX910-SW-99003-r4p0-00rel0.tgz
	tar -zxf DX910-SW-99006-r4p0-00rel0.tgz
	tar -zxf lib_mali_r4p0.tar.gz
}

addtask do_unpack_subarchives after do_unpack before do_configure

do_compile_modules() {
	echo "#####################################"
	echo "          Compiling modules          "
	echo "#####################################"
	export CONFIG=pb-virtex5
	export KDIR=${STAGING_KERNEL_BUILDDIR}
	cd ${S}/DX910-SW-99002-r4p0-00rel0_modify/driver/src/devicedrv/ump/
	module_do_compile
	cd ${S}/DX910-SW-99002-r4p0-00rel0_modify/driver/src/devicedrv/umplock/
	module_do_compile
	unset CONFIG
	export USING_MMU=0
	export USING_UMP=1
	export USING_PMM=0
	export MALI_PLATFORM=
	cd ${S}/DX910-SW-99002-r4p0-00rel0_modify/driver/src/devicedrv/mali/
	module_do_compile
	cd ${S}/DX910-SW-99002-r4p0-00rel0_modify/driver/src/egl/x11/drm_module/mali_drm
	module_do_compile
	unset USING_MMU USING_UMP USING_PMM MALI_PLATFORM
}

addtask do_compile_modules after do_compile before do_install

do_compile_libump() {
	echo "#####################################"
	echo "          Compiling libUMP           "
	echo "#####################################"
	cd ${S}/DX910-SW-99006-r4p0-00rel0/driver/src/ump
	oe_runmake all
}

addtask do_compile_libump after do_compile before do_install

do_compile_libmali() {
	echo "#####################################"
	echo "          Compiling libmali          "
	echo "#####################################"
	cd ${S}/lib_mali
	rm -f libEGL.so libEGL.so.1 libEGL.so.1.4 libGLESv1_CM.so libGLESv1_CM.so.1 libGLESv1_CM.so.1.1 libGLESv2.so libGLESv2.so.2 libGLESv2.so.2.0 
	rm -f libUMP.so
}

addtask do_compile_libmali after do_compile before do_install

do_compile() {
	echo "All tasks are compiled"
}

do_install_modules() {
	echo "#####################################"
	echo "          Installing modules          "
	echo "#####################################"
	export CONFIG=pb-virtex5
	export KDIR=${STAGING_KERNEL_BUILDDIR}
	cd ${S}/DX910-SW-99002-r4p0-00rel0_modify/driver/src/devicedrv/ump/
	module_do_install
	cd ${S}/DX910-SW-99002-r4p0-00rel0_modify/driver/src/devicedrv/umplock/
	module_do_install
	unset CONFIG
	export USING_MMU=1 
	export USING_UMP=1
	export USING_PMM=1
	export MALI_PLATFORM=arm
	cd ${S}/DX910-SW-99002-r4p0-00rel0_modify/driver/src/devicedrv/mali/
	module_do_install
	cd ${S}/DX910-SW-99002-r4p0-00rel0_modify/driver/src/egl/x11/drm_module/mali_drm
	module_do_install
	unset USING_MMU USING_UMP USING_PMM MALI_PLATFORM
}

addtask do_install_modules after do_install before do_package

do_install_libump() {
	echo "#####################################"
	echo "          Installing libUMP          "
	echo "#####################################"
	cd ${S}/DX910-SW-99006-r4p0-00rel0/driver/src/ump
	install -d ${D}${libdir}
	install --mode=0644 libUMP.so ${D}${libdir}/libUMP.so.1.0
	install --mode=0644 libUMP.a ${D}${libdir}/libUMP.a
	install -d ${D}${includedir}
	cp -r include/* ${D}${includedir}/
	cd ${D}${libdir}
	rm -f libUMP.so
	ln -s libUMP.so.1.0 libUMP.so
}

addtask do_install_libump after do_install before do_package

do_install_libmali() {
	echo "#####################################"
	echo "          Installing libmali          "
	echo "#####################################"
	cd ${S}/lib_mali
	install -d ${D}${libdir}
	install --mode=0644 libMali.so ${D}${libdir}/libMali.so.1.0
	install -d ${D}${includedir}
	cp -r include/* ${D}${includedir}/
	cd ${D}${libdir}
	rm -f libMali.so libEGL.so.1.4.9 libEGL.so libGLESv1_CM.so.1.1.9 libGLESv1_CM.so libGLESv2.so.2.0.9 libGLESv2.so
	ln -s libMali.so.1.0 libMali.so
	ln -s libMali.so.1.0 libEGL.so.1.4.9
	ln -s libMali.so.1.0 libEGL.so
	ln -s libMali.so.1.0 libGLESv1_CM.so.1.1.9
	ln -s libMali.so.1.0 libGLESv1_CM.so
	ln -s libMali.so.1.0 libGLESv2.so.2.0.9
	ln -s libMali.so.1.0 libGLESv2.so
}

addtask do_install_libmali after do_install before do_package

do_install() {
	echo "All tasks are installed"
}

