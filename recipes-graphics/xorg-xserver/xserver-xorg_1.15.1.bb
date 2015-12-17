require xserver-xorg.inc

# Misc build failure for master HEAD
SRC_URI += "file://fix_open_max_preprocessor_error.patch \
            file://xorg-CVE-2013-6424.patch \
            file://xshmfence-option.patch \
            file://Fix-subwindow-in-Xi-emulated-events.patch \
            file://xtrans.patch \
            file://0001-xkb-Don-t-swap-XkbSetGeometry-data-in-the-input-buff.patch \
            file://0001-xkb-Check-strings-length-against-request-size.patch \
           "

SRC_URI[md5sum] = "e4c70262ed89764be8f8f5d699ed9227"
SRC_URI[sha256sum] = "626db6882602ebe1ff81f7a4231c7ccc6ceb5032f2b5b3954bf749e1567221e2"

PACKAGECONFIG[systemd-logind] = ""

# These extensions are now integrated into the server, so declare the migration
# path for in-place upgrades.

RREPLACES_${PN} =  "${PN}-extension-dri \
                    ${PN}-extension-dri2 \
                    ${PN}-extension-record \
                    ${PN}-extension-extmod \
                    ${PN}-extension-dbe \
                   "
RPROVIDES_${PN} =  "${PN}-extension-dri \
                    ${PN}-extension-dri2 \
                    ${PN}-extension-record \
                    ${PN}-extension-extmod \
                    ${PN}-extension-dbe \
                   "
RCONFLICTS_${PN} = "${PN}-extension-dri \
                    ${PN}-extension-dri2 \
                    ${PN}-extension-record \
                    ${PN}-extension-extmod \
                    ${PN}-extension-dbe \
                   "
