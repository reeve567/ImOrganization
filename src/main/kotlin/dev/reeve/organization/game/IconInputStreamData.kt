package dev.reeve.organization.game

import java.io.InputStream

data class IconInputStreamData(var iconInputStream: InputStream?, val type: IconInputType)

enum class IconInputType {
	PNG, ICO
}