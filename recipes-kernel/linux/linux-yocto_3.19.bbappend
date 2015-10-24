FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

PR := "${PR}.34"

COMPATIBLE_MACHINE_orangepiplus = "orangepiplus"

KERNEL_FEATURES_append_orangepiplus += " cfg/smp.scc"

SRC_URI += "file://orangepiplus-standard.scc \
            file://orangepiplus-user-config.cfg \
            file://orangepiplus-user-patches.scc \
            file://orangepiplus-user-features.scc \
            file://defconfig \
           "

# uncomment and replace these SRCREVs with the real commit ids once you've had
# the appropriate changes committed to the upstream linux-yocto repo
#SRCREV_machine_pn-linux-yocto_orangepiplus ?= "840bb8c059418c4753415df56c9aff1c0d5354c8"
#SRCREV_meta_pn-linux-yocto_orangepiplus ?= "4fd76cc4f33e0afd8f906b1e8f231b6d13b6c993"
#LINUX_VERSION = "3.19"
