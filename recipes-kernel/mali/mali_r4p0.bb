
#
# Bitbake file to compile the Mali-400 kernel drivers.
#

DESCRIPTION = "Mali-400 kernel drivers for external compilation for use with linux-sunxi kernel. "
SECTION = "drivers"
DEPENDS = "virtual/kernel mesa-gl"
PROVIDES = "virtual/mali libump libmali virtual/libgles1 virtual/libgles2 virtual/egl"
RPROVIDES_libmali = "libEGL.so libGLESv2.so libGLESv1_CM.so libMali.so"
RPROVIDES_libump = "libUMP.so"
LICENSE = "GPLv2&Apache-2.0"
LIC_FILES_CHKSUM = "file://${MODULE_ARCHIVE}/driver/src/devicedrv/mali/linux/mali_kernel_linux.c;beginline=1;endline=9;md5=7a6e4d35599c2043cf0d8c2a15cc7cc5 \
	file://${LIBUMP_ARCHIVE}/driver/src/ump/readme.txt;beginline=1;endline=15;md5=b67f3604f0a4f7643e728cb817b7989b"
PACKAGE_ARCH = "${MACHINE_ARCH}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_URI = "file://mali-ddk-r4p0.tar.gz"

SRCREV = ""

S = "${WORKDIR}/mali-ddk-r4p0"

OVERRIDES .= "${@bb.utils.contains("TUNE_FEATURES", "vfpv4", ":vfpv4", "", d)}"

EXTRA_OECONF = ""
EXTRA_OEMAKE = ""

inherit module

PACKAGES += "libump libump-dev libump-dbg libump-staticdev libmali libmali-dev libmali-dbg"
FILES_${PN} = ""
FILES_${PN}-dev = ""
FILES_${PN}-dbg = ""
FILES_libump = "${libdir}/libUMP.s*"
FILES_libump-dev = "${includedir}/ump"
FILES_libump-staticdev = "${libdir}/libUMP.a"
FILES_libump-dbg = "${libdir}/.debug/libUMP* /usr/src"
FILES_libmali = "${libdir}/libEGL.s* ${libdir}/libGLESv1_CM.s* ${libdir}/libGLESv2.s* ${libdir}/libMali.s*"
FILES_libmali-dev = "${includedir}/EGL ${includedir}/GLES ${includedir}/GLES2 ${includedir}/KHR ${libdir}/pkgconfig/egl.pc ${libdir}/pkgconfig/glesv1.pc ${libdir}/pkgconfig/glesv2.pc"
FILES_libmali-dbg = "${libdir}/.debug/libMali*"
INSANE_SKIP_libmali += "dev-so"
INSANE_SKIP_libump += "dev-so"

MODULE_ARCHIVE = "DX910-SW-99002-r4p0-00rel0_modify"
LIBUMP_ARCHIVE = "DX910-SW-99006-r4p0-00rel0"
LIBMALI_ARCHIVE = "lib_mali"

do_unpack_subarchives () {
	tar -zxf ${MODULE_ARCHIVE}.tgz
	#tar -zxf DX910-SW-99003-r4p0-00rel0.tgz
	tar -zxf ${LIBUMP_ARCHIVE}.tgz
	tar -zxf ${LIBMALI_ARCHIVE}_r4p0.tar.gz
}

addtask do_unpack_subarchives after do_unpack before do_configure

do_compile_modules() {
	echo "#####################################"
	echo "          Compiling modules          "
	echo "#####################################"
	export CONFIG=ca8-virtex820-m400-1
	export KDIR=${STAGING_KERNEL_BUILDDIR}
	cd ${S}/${MODULE_ARCHIVE}/driver/src/devicedrv/ump/
	module_do_compile
	cd ${S}/${MODULE_ARCHIVE}/driver/src/devicedrv/umplock/
	module_do_compile
	unset CONFIG
	#export USING_MMU=1
	export USING_UMP=1
	#export USING_PMM=1
	export BUILD=debug
	export MALI_PLATFORM=
	cd ${S}/${MODULE_ARCHIVE}/driver/src/devicedrv/mali/
	module_do_compile
	cd ${S}/${MODULE_ARCHIVE}/driver/src/egl/x11/drm_module/mali_drm
	module_do_compile
	unset USING_MMU USING_UMP USING_PMM MALI_PLATFORM BUILD
}

addtask do_compile_modules after do_compile before do_install

do_compile_libump() {
	echo "#####################################"
	echo "          Compiling libUMP           "
	echo "#####################################"
	cd ${S}/${LIBUMP_ARCHIVE}/driver/src/ump
	oe_runmake all
}

addtask do_compile_libump after do_compile before do_install

do_compile_libmali() {
	echo "#####################################"
	echo "          Compiling libmali          "
	echo "#####################################"
	cd ${S}/${LIBMALI_ARCHIVE}
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
	export CONFIG=ca8-virtex820-m400-1
	export KDIR=${STAGING_KERNEL_BUILDDIR}
	cd ${S}/${MODULE_ARCHIVE}/driver/src/devicedrv/ump/
	module_do_install
	cd ${S}/${MODULE_ARCHIVE}/driver/src/devicedrv/umplock/
	module_do_install
	unset CONFIG
	export USING_MMU=1 
	export USING_UMP=1
	export USING_PMM=1
	export MALI_PLATFORM=
	cd ${S}/${MODULE_ARCHIVE}/driver/src/devicedrv/mali/
	module_do_install
	cd ${S}/${MODULE_ARCHIVE}/driver/src/egl/x11/drm_module/mali_drm
	module_do_install
	unset USING_MMU USING_UMP USING_PMM MALI_PLATFORM
}

addtask do_install_modules after do_install before do_package

do_install_libump() {
	echo "#####################################"
	echo "          Installing libUMP          "
	echo "#####################################"
	cd ${S}/${LIBUMP_ARCHIVE}/driver/src/ump
	install -d ${D}${libdir}
	install --mode=0644 libUMP.so ${D}${libdir}/libUMP.so.1.0
	install --mode=0644 libUMP.a ${D}${libdir}/libUMP.a
	install -d ${D}${includedir}
	cp -r include/* ${D}${includedir}/
	cd ${D}${libdir}
	rm -f libUMP.so
	ln -s libUMP.so.1.0 libUMP.so
	ln -s libUMP.so.1.0 libUMP.so.1
}

addtask do_install_libump after do_install before do_package

do_install_libmali() {
	echo "#####################################"
	echo "          Installing libmali          "
	echo "#####################################"
	cd ${S}/${LIBMALI_ARCHIVE}
	install -d ${D}${libdir}
	install --mode=0644 libMali.so ${D}${libdir}/libMali.so.1.0
	install -d ${D}${includedir}
	cp -r include/khronos/* ${D}${includedir}/
	install -d ${D}${libdir}/pkgconfig
	install --mode=644 pkgconfig/egl.pc ${D}${libdir}/pkgconfig/egl.pc
	install --mode=644 pkgconfig/glesv1.pc ${D}${libdir}/pkgconfig/glesv1.pc
	install --mode=644 pkgconfig/glesv2.pc ${D}${libdir}/pkgconfig/glesv2.pc
	cd ${D}${libdir}
	rm -f libMali.so libEGL.so.1.4.9 libEGL.so libEGL.so.1 libGLESv1_CM.so.1.1.9 libGLESv1_CM.so libGLESv1_CM.so.1 libGLESv2.so.2.0.9 libGLESv2.so libGLESv2.so.2
	ln -s libMali.so.1.0 libMali.so
	ln -s libMali.so.1.0 libEGL.so.1.4.9
	ln -s libMali.so.1.0 libEGL.so.1
	ln -s libMali.so.1.0 libEGL.so
	ln -s libMali.so.1.0 libGLESv1_CM.so.1.1.9
	ln -s libMali.so.1.0 libGLESv1_CM.so.1
	ln -s libMali.so.1.0 libGLESv1_CM.so
	ln -s libMali.so.1.0 libGLESv2.so.2.0.9
	ln -s libMali.so.1.0 libGLESv2.so.2
	ln -s libMali.so.1.0 libGLESv2.so
}

addtask do_install_libmali after do_install before do_package

do_install() {
	echo "All tasks are installed"
}

