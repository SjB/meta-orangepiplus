DESCRIPTION = "Yocto Kernel"
SECTION = "kernel"
LICENSE = "GPLv2"

DEPENDS = "u-boot-mkimage"

LIC_FILES_CHKSUM = "file://linux-3.4/COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

INC_PR = "r4"

DEPENDS += "xz-native bc-native"
DEPENDS_append_aarch64 = " libgcc"
KERNEL_CC_append_aarch64 = " ${TOOLCHAIN_OPTIONS}"
KERNEL_LD_append_aarch64 = " ${TOOLCHAIN_OPTIONS}"

inherit linux-kernel-base deploy

# A KMACHINE is the mapping of a yocto $MACHINE to what is built
# by the kernel. This is typically the branch that should be built,
# and it can be specific to the machine or shared
# KMACHINE = "UNDEFINED"

LINUX_KERNEL_TYPE ?= "standard"

# KMETA ?= ""
KBRANCH ?= "master"
KMACHINE ?= "${MACHINE}"
SRCREV_FORMAT ?= "meta_machine" 

# LEVELS:
#   0: no reporting
#   1: report options that are specified, but not in the final config
#   2: report options that are not hardware related, but set by a BSP
KCONF_AUDIT_LEVEL ?= "1"
KCONF_BSP_AUDIT_LEVEL ?= "0"

LINUX_VERSION_EXTENSION ?= "-yocto-${LINUX_KERNEL_TYPE}"

S = "${WORKDIR}/git"

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-yocto_3.4-orangepi:"

# Override SRC_URI in a bbappend file to point at a different source
# tree if you do not want to build from Linus' tree.
SRC_URI = "git://github.com/loboris/OrangePI-Kernel.git;protocol=https;name=machine"

SRC_URI += "file://0001-fix-makefiles.patch \
	file://0002-remote-build-dir.patch"

# Override SRCREV to point to a different commit in a bbappend file to
# build a different release of the Linux kernel.
# tag: v3.4 76e10d158efb6d4516018846f60c2ab5501900bc
SRCREV_machine="3516ca7cb00686b1a6008e0f01fdd93b9719d36c"

LINUX_VERSION ?= "3.4"
LINUX_VERSION_EXTENSION ?= "-orangepi"

#KERNEL_VERSION = "${@get_kernelversion_headers('${STAGING_KERNEL_DIR}')}"
KERNEL_VERSION = "3.4.39-01-lobo"

PR = "r2"
PV = "${LINUX_VERSION}+git${SRCPV}"

# Override COMPATIBLE_MACHINE to include your machine in a bbappend
# file. Leaving it empty here ensures an early explicit build failure.
COMPATIBLE_MACHINE = "orangepiplus|orangepi2"

PACKAGE_ARCH = "${MACHINE_ARCH}"

EXTRA_OEMAKE = ""

PACKAGES_DYNAMIC += "^kernel-module-.*"
PACKAGES_DYNAMIC += "^kernel-image-.*"
PACKAGES_DYNAMIC += "^kernel-firmware-.*"

export OS = "${TARGET_OS}"
export CROSS_COMPILE = "${TARGET_PREFIX}"

KERNEL_PRIORITY ?= "${@int(d.getVar('PV',1).split('-')[0].split('+')[0].split('.')[0]) * 10000 + \
                       int(d.getVar('PV',1).split('-')[0].split('+')[0].split('.')[1]) * 100 + \
                       int(d.getVar('PV',1).split('-')[0].split('+')[0].split('.')[-1])}"

KERNEL_RELEASE ?= "${KERNEL_VERSION}"

ARCH ?= "arm"

# Where built kernel lies in the kernel tree
KERNEL_OUTPUT ?= "arch/${ARCH}/boot/${KERNEL_IMAGETYPE}"
KERNEL_IMAGEDEST = "boot"


PROVIDES += "virtual/kernel"
DEPENDS += "virtual/${TARGET_PREFIX}binutils virtual/${TARGET_PREFIX}gcc kmod-native depmodwrapper-cross bc-native"

#S = "${STAGING_KERNEL_DIR}"
#B = "${WORKDIR}/build"
KBUILD_OUTPUT = "${B}"
OE_TERMINAL_EXPORTS += "KBUILD_OUTPUT"

# we include gcc above, we dont need virtual/libc
INHIBIT_DEFAULT_DEPS = "1"

KERNEL_IMAGETYPE ?= "zImage"
INITRAMFS_IMAGE ?= ""
INITRAMFS_TASK ?= ""
INITRAMFS_IMAGE_BUNDLE ?= ""


# kernel-base becomes kernel-${KERNEL_VERSION}
# kernel-image becomes kernel-image-${KERNEL_VERISON}
PACKAGES = "kernel kernel-base kernel-vmlinux kernel-image kernel-dev kernel-modules"
FILES_${PN} = ""
FILES_kernel-base = "/lib/modules/${KERNEL_VERSION}/modules.order /lib/modules/${KERNEL_VERSION}/modules.builtin ${sysconfdir}/*"
FILES_kernel-image = "/boot/${KERNEL_IMAGETYPE}*"
FILES_kernel-dev = "/boot/System.map* /boot/Module.symvers* /boot/config* ${KERNEL_SRC_PATH} /lib/modules/${KERNEL_VERSION}/build $"
FILES_kernel-vmlinux = "/boot/vmlinux*"
FILES_kernel-modules = "/lib/modules/${KERNEL_VERSION}/kernel/*"
RDEPENDS_kernel = "kernel-base"
# Allow machines to override this dependency if kernel image files are 
# not wanted in images as standard
RDEPENDS_kernel-base ?= "kernel-image"
PKG_kernel-image = "kernel-image-${@legitimize_package_name('${KERNEL_VERSION}')}"
PKG_kernel-base = "kernel-${@legitimize_package_name('${KERNEL_VERSION}')}"
RPROVIDES_kernel-base += "kernel-${KERNEL_VERSION}"
ALLOW_EMPTY_kernel = "1"
ALLOW_EMPTY_kernel-base = "1"
ALLOW_EMPTY_kernel-image = "1"
ALLOW_EMPTY_kernel-modules = "1"
DESCRIPTION_kernel-modules = "Kernel modules meta package"

do_unpack[cleandirs] += " ${S} ${STAGING_KERNEL_DIR} ${B} ${STAGING_KERNEL_BUILDDIR}"
do_clean[cleandirs] += " ${S} ${STAGING_KERNEL_DIR} ${B} ${STAGING_KERNEL_BUILDDIR}"
base_do_unpack_append () {
    s = d.getVar("S", True)
    s += "/linux-3.4"
    if s[-1] == '/':
        # drop trailing slash, so that os.symlink(kernsrc, s) doesn't use s as directory name and fail
        s=s[:-1]
    kernsrc = d.getVar("STAGING_KERNEL_DIR", True)
    if s != kernsrc:
        bb.utils.mkdirhier(kernsrc)
        bb.utils.remove(kernsrc, recurse=True)
        import subprocess
        subprocess.call(d.expand("mv ${S}/linux-3.4 ${STAGING_KERNEL_DIR}"), shell=True)
        os.symlink(kernsrc, s)
}

inherit cml1

do_configure() {
	echo Not supported architecture
	exit 1
}

do_create_rootfs_cpio() {
	cd build

	rm -f rootfs-lobo.img.gz

	# create new rootfs cpio
	cd rootfs-test1
	mkdir --parents run
	mkdir --parents conf/conf.d

	find . | cpio --quiet -o -H newc > ../rootfs-lobo.img
	cd ..
	gzip rootfs-lobo.img
	cd ..

	cd ${STAGING_KERNEL_BUILDDIR}
	mkdir -p output/lib
	cp ${S}/build/rootfs-lobo.img.gz output/rootfs.cpio.gz

}

addtask do_create_rootfs_cpio before do_configure after do_patch

do_configure_orangepiplus() {
	cd ${STAGING_KERNEL_BUILDDIR}
	
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE CROSS_COMPILE
	export PATH="${S}/brandy/gcc-linaro/bin":"$PATH"

	cat ${S}/build/sun8iw7p1smp_lobo_defconfig | sed '/CONFIG_GETH_CLK_SYS=y/a CONFIG_GMAC_PHY_POWER=y' > ${S}/build/sun8iw7p1smp_lobo_defconfig.xx1
	cat ${S}/build/sun8iw7p1smp_lobo_defconfig.xx1 | sed s/"CONFIG_USB_SUNXI_OHCI0=y"/"# CONFIG_USB_SUNXI_OHCI0 is not set"/g > ${S}/build/sun8iw7p1smp_lobo_defconfig.xx2
	cat ${S}/build/sun8iw7p1smp_lobo_defconfig.xx2 | sed s/"CONFIG_USB_SUNXI_OHCI2=y"/"# CONFIG_USB_SUNXI_OHCI2 is not set"/g > ${S}/build/sun8iw7p1smp_lobo_defconfig.xx3
	cat ${S}/build/sun8iw7p1smp_lobo_defconfig.xx3 | sed s/"CONFIG_USB_SUNXI_OHCI3=y"/"# CONFIG_USB_SUNXI_OHCI3 is not set"/g > ${S}/build/sun8iw7p1smp_lobo_defconfig.xx4
	cp ${S}/build/Kconfig.piplus ${STAGING_KERNEL_DIR}/drivers/net/ethernet/sunxi/eth/Kconfig
	cp ${S}/build/sunxi_geth.c.piplus ${STAGING_KERNEL_DIR}/drivers/net/ethernet/sunxi/eth/sunxi_geth.c
	cp ${S}/build/sun8iw7p1smp_lobo_defconfig.xx4 ${STAGING_KERNEL_DIR}/arch/arm/configs/sun8iw7p1smp_lobo_defconfig
	rm ${S}/build/sun8iw7p1smp_lobo_defconfig.xx?

	echo "  Configuring ..."
	oe_runmake -C ${STAGING_KERNEL_DIR} ARCH=${ARCH} ${PARALLEL_MAKE} sun8iw7p1smp_lobo_defconfig CC="${KERNEL_CC}" LD="${KERNEL_LD}" ${KERNEL_EXTRA_ARGS} O=${STAGING_KERNEL_BUILDDIR} CROSS_COMPILE=arm-linux-gnueabi-
}

do_compile() {
	cd ${STAGING_KERNEL_DIR}
	export PATH="${S}/brandy/gcc-linaro/bin":"$PATH"

	bbnote Building kernel

	#CC="${KERNEL_CC}" LD="${KERNEL_LD}"
	
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE CROSS_COMPILE

	oe_runmake -j 1 ${KERNEL_IMAGETYPE} ARCH=${ARCH} ${KERNEL_EXTRA_ARGS} O=${STAGING_KERNEL_BUILDDIR} CROSS_COMPILE=arm-linux-gnueabi- V=1
	bbnote Compile finished
}

do_shared_workdir() {
	echo "${KERNEL_VERSION}" > ${STAGING_KERNEL_BUILDDIR}/kernel-abiversion
}

addtask shared_workdir after do_compile before do_compile_kernelmodules

do_compile_kernelmodules() {
	cd ${STAGING_KERNEL_DIR}
	export PATH="${S}/brandy/gcc-linaro/bin":"$PATH"

	bbnote Building kernel modules

	#CC="${KERNEL_CC}" LD="${KERNEL_LD}"
	
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE CROSS_COMPILE

	oe_runmake modules ARCH=${ARCH} ${KERNEL_EXTRA_ARGS} O=${STAGING_KERNEL_BUILDDIR} CROSS_COMPILE=arm-linux-gnueabi- 
	bbnote Compile finished
}

addtask compile_kernelmodules after do_compile before do_install

do_install() {
	cd ${STAGING_KERNEL_DIR}
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE CROSS_COMPILE
	export PATH="${S}/brandy/gcc-linaro/bin":"$PATH"
	export CROSS_COMPILE=arm-linux-gnueabi-
	# 
	# First install the modules
	#
	oe_runmake ARCH=${ARCH} DEPMOD=echo INSTALL_MOD_PATH="${D}" modules_install O=${STAGING_KERNEL_BUILDDIR}
	rm "${D}/lib/modules/${KERNEL_VERSION}/build"
	rm "${D}/lib/modules/${KERNEL_VERSION}/source"
	# If the kernel/ directory is empty remove it to prevent QA issues
	rmdir --ignore-fail-on-non-empty "${D}/lib/modules/${KERNEL_VERSION}/kernel"

	cd ${STAGING_KERNEL_BUILDDIR}
	#
	# Install various kernel output (zImage, map file, config, module support files)
	#
	install -d ${D}/${KERNEL_IMAGEDEST}
	install -d ${D}/boot
	install -m 0644 ${KERNEL_OUTPUT} ${D}/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
	install -m 0644 System.map ${D}/boot/System.map-${KERNEL_VERSION}
	install -m 0644 .config ${D}/boot/config-${KERNEL_VERSION}
	install -m 0644 vmlinux ${D}/boot/vmlinux-${KERNEL_VERSION}
	[ -e Module.symvers ] && install -m 0644 Module.symvers ${D}/boot/Module.symvers-${KERNEL_VERSION}
	install -d ${D}${sysconfdir}/modules-load.d
	install -d ${D}${sysconfdir}/modprobe.d
}

do_bundle_initramfs () {
	:
}

addtask bundle_initramfs after do_install before do_deploy

KERNEL_IMAGE_BASE_NAME ?= "${KERNEL_IMAGETYPE}-${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
# Don't include the DATETIME variable in the sstate package signatures
KERNEL_IMAGE_BASE_NAME[vardepsexclude] = "DATETIME"
KERNEL_IMAGE_SYMLINK_NAME ?= "${KERNEL_IMAGETYPE}-${MACHINE}"
MODULE_IMAGE_BASE_NAME ?= "modules-${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
MODULE_IMAGE_BASE_NAME[vardepsexclude] = "DATETIME"
MODULE_TARBALL_BASE_NAME ?= "${MODULE_IMAGE_BASE_NAME}.tgz"
# Don't include the DATETIME variable in the sstate package signatures
MODULE_TARBALL_SYMLINK_NAME ?= "modules-${MACHINE}.tgz"
MODULE_TARBALL_DEPLOY ?= "1"

do_deploy() {
	cd ${STAGING_KERNEL_BUILDDIR}
	install -m 0644 ${KERNEL_OUTPUT} ${DEPLOYDIR}/${KERNEL_IMAGE_BASE_NAME}.bin
	if [ ${MODULE_TARBALL_DEPLOY} = "1" ] && (grep -q -i -e '^CONFIG_MODULES=y$' .config); then
		mkdir -p ${D}/lib
		tar -cvzf ${DEPLOYDIR}/${MODULE_TARBALL_BASE_NAME} -C ${D} lib
		ln -sf ${MODULE_TARBALL_BASE_NAME} ${DEPLOYDIR}/${MODULE_TARBALL_SYMLINK_NAME}
	fi

	ln -sf ${KERNEL_IMAGE_BASE_NAME}.bin ${DEPLOYDIR}/${KERNEL_IMAGE_SYMLINK_NAME}.bin
	ln -sf ${KERNEL_IMAGE_BASE_NAME}.bin ${DEPLOYDIR}/${KERNEL_IMAGETYPE}

	cp ${COREBASE}/meta/files/deploydir_readme.txt ${DEPLOYDIR}/README_-_DO_NOT_DELETE_FILES_IN_THIS_DIRECTORY.txt

	cd ${B}
	# Update deploy directory
	if [ -e "${KERNEL_OUTPUT}.initramfs" ]; then
		echo "Copying deploy kernel-initramfs image and setting up links..."
		initramfs_base_name=${INITRAMFS_BASE_NAME}
		initramfs_symlink_name=${KERNEL_IMAGETYPE}-initramfs-${MACHINE}
		install -m 0644 ${KERNEL_OUTPUT}.initramfs ${DEPLOYDIR}/${initramfs_base_name}.bin
		cd ${DEPLOYDIR}
		ln -sf ${initramfs_base_name}.bin ${initramfs_symlink_name}.bin
	fi
}
do_deploy[dirs] = "${DEPLOYDIR} ${B}"
do_deploy[prefuncs] += "package_get_auto_pr"

addtask deploy after do_populate_sysroot

# We don't need to stage anything, not the modules/firmware since those would clash with linux-firmware
sysroot_stage_all () {
	:
}

python do_menuconfig() {
    import shutil

    try:
        mtime = os.path.getmtime(".config")
        shutil.copy(".config", ".config.orig")
    except OSError:
        mtime = 0

    oe_terminal("${SHELL} -c \"cd ../git; ./config_linux_kernel.sh; if [ \$? -ne 0 ]; then echo 'Command failed.'; printf 'Press any key to continue... '; read r; fi\"", '${PN} Configuration', d)

    # FIXME this check can be removed when the minimum bitbake version has been bumped
    if hasattr(bb.build, 'write_taint'):
        try:
            newmtime = os.path.getmtime(".config")
        except OSError:
            newmtime = 0

        if newmtime > mtime:
            bb.note("Configuration changed, recompile will be forced")
            bb.build.write_taint('do_compile', d)
}
