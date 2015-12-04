DESCRIPTION = "Video and audio decode/encode libraries"
SECTION = "multimedia"
DEPENDS = ""
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=83af8811a28727a13f04132cc33b7f58"

SRC_URI = "git://github.com/allwinner-zh/media-codec.git;protocol=https"

S = "${WORKDIR}/git/sunxi-cedarx/SOURCE"

inherit autotools pkgconfig
