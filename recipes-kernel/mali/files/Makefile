SRC-ump := $(shell pwd)/driver/src/devicedrv/ump/
SRC-mali := $(shell pwd)/driver/src/devicedrv/mali/
SRC-drm := $(shell pwd)/driver/src/egl/x11/drm_module/mali_drm

MALI_TARGETS ?= "ump mali drm"
MALI_CLEAN_TARGETS = $(addsuffix _clean,$(MALI_TARGETS))
MALI_INSTALL_TARGETS = $(addsuffix _install,$(MALI_TARGETS))

all: $(MALI_TARGETS)

clean: $(MALI_CLEAN_TARGETS)

modules_install: $(MALI_INSTALL_TARGETS)

mali: ump
	$(MAKE) -C $(SRC-mali) KDIR=$(KERNEL_SRC) CONFIG=$(MALI_CONFIG) TARGET_PLATFORM=$(MALI_PLATFORM) SVN_REV=0

ump:
	$(MAKE) -C $(SRC-ump) KDIR=$(KERNEL_SRC) CONFIG=$(UMP_CONFIG) SVN_REV=0

drm: mali
	$(MAKE) -C $(SRC-drm) KDIR=$(KERNEL_SRC) CONFIG=$(UMP_CONFIG) SVN_REV=0

mali_install: mali
	$(MAKE) -C $(KERNEL_SRC) M=$(SRC-mali) SVN_REV=0 modules_install

ump_install: ump
	$(MAKE) -C $(KERNEL_SRC) M=$(SRC-ump) SVN_REV=0 modules_install

drm_install: drm
	$(MAKE) -C $(KERNEL_SRC) M=$(SRC-drm) SVN_REV=0 modules_install

mali_clean:
	$(MAKE) -C $(SRC-mali) KDIR=$(KERNEL_SRC) CONFIG=$(MALI_CONFIG) TARGET_PLATFORM=$(MALI_PLATFORM) SVN_REV=0 clean

ump_clean:
	$(MAKE) -C $(SRC-ump) KDIR=$(KERNEL_SRC) CONFIG=$(UMP_CONFIG) SVN_REV=0 clean

drm_clean:
	$(MAKE) -C $(SRC-drm) KDIR=$(KERNEL_SRC) CONFIG=$(UMP_CONFIG) SVN_REV=0 clean
