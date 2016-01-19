object A {
	@JvmStatic fun main(args: Array<String>) {
		val split = args
		var path = ""
		var i = 5
		while (i < split.size) {
			val s = split[i].trim { it <= ' ' }
			if (!s.isEmpty()) {
				path += split[i]
			}
			if (s.isEmpty() && ++i > split.size) {
				break
			} else if (s.isEmpty() && !split[i].trim { it <= ' ' }.isEmpty()) {
				path += split[i]
			}
			i++
		}
	}
}
