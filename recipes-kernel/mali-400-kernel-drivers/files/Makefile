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
	$(MAKE) -C $(SRC-mali) KDIR=$(KERNEL_SRC) CONFIG=$(MALI_CONFIG) TARGET_PLATFORM=$(MALI_PLATFORM) 

ump:
	$(MAKE) -C $(SRC-ump) KDIR=$(KERNEL_SRC) CONFIG=$(UMP_CONFIG)

drm: mali
	$(MAKE) -C $(SRC-drm) KDIR=$(KERNEL_SRC) CONFIG=$(UMP_CONFIG)

mali_install: mali
	$(MAKE) -C $(KERNEL_SRC) M=$(SRC-mali) modules_install

ump_install: ump
	$(MAKE) -C $(KERNEL_SRC) M=$(SRC-ump) modules_install

drm_install: drm
	$(MAKE) -C $(KERNEL_SRC) M=$(SRC-drm) modules_install

mali_clean:
	$(MAKE) -C $(SRC-mali) KDIR=$(KERNEL_SRC) CONFIG=$(MALI_CONFIG) TARGET_PLATFORM=$(MALI_PLATFORM) clean

ump_clean:
	$(MAKE) -C $(SRC-ump) KDIR=$(KERNEL_SRC) CONFIG=$(UMP_CONFIG) clean

drm_clean:
	$(MAKE) -C $(SRC-drm) KDIR=$(KERNEL_SRC) CONFIG=$(UMP_CONFIG) clean
