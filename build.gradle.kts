import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val lwjglVersion = "3.2.3"
val lwjglNatives = "natives-windows"

plugins {
	kotlin("jvm") version "1.4.31"
	id("com.github.johnrengelman.shadow") version "6.1.0"
	java
}

group = "dev.reeve"
version = "1.0-SNAPSHOT"

repositories {
	maven("https://jitpack.io")
	mavenCentral()
	jcenter()
}

dependencies {
	testImplementation(kotlin("test-junit"))
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.21")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
	
	implementation("org.ini4j:ini4j:0.5.4") // ini file lib
	implementation("org.jclarion:image4j:0.7") // ico file lib
	
	//implementation("khttp:khttp:1.0.0") // http request lib [no work with java 11 :(]
	implementation("com.github.kittinunf.fuel:fuel:2.3.1") // alternative to khttp :/
	implementation("com.github.kittinunf.fuel:fuel-coroutines:2.3.1")
	
	implementation("org.jsoup:jsoup:1.13.1") // http page lib
	implementation("com.google.code.gson:gson:2.8.6") // json lib
	
	implementation("org.apache.commons:commons-compress:1.20") // apache commons (.zip, .gz, .tar, etc)
	implementation("org.tukaani:xz:1.8") // 7z
	implementation("com.github.axet:java-unrar:1.7.0-8") //rar
	
	implementation("com.github.kotlin-graphics:imgui:v1.79") // imgui
	
	//lwjgl
	implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
	
	implementation("org.lwjgl:lwjgl")
	implementation("org.lwjgl:lwjgl-assimp")
	implementation("org.lwjgl:lwjgl-bgfx")
	implementation("org.lwjgl:lwjgl-cuda")
	implementation("org.lwjgl:lwjgl-egl")
	implementation("org.lwjgl:lwjgl-glfw")
	implementation("org.lwjgl:lwjgl-jawt")
	implementation("org.lwjgl:lwjgl-jemalloc")
	implementation("org.lwjgl:lwjgl-libdivide")
	implementation("org.lwjgl:lwjgl-llvm")
	implementation("org.lwjgl:lwjgl-lmdb")
	implementation("org.lwjgl:lwjgl-lz4")
	implementation("org.lwjgl:lwjgl-meow")
	implementation("org.lwjgl:lwjgl-nanovg")
	implementation("org.lwjgl:lwjgl-nfd")
	implementation("org.lwjgl:lwjgl-nuklear")
	implementation("org.lwjgl:lwjgl-odbc")
	implementation("org.lwjgl:lwjgl-openal")
	implementation("org.lwjgl:lwjgl-opencl")
	implementation("org.lwjgl:lwjgl-opengl")
	implementation("org.lwjgl:lwjgl-opengles")
	implementation("org.lwjgl:lwjgl-openvr")
	implementation("org.lwjgl:lwjgl-opus")
	implementation("org.lwjgl:lwjgl-ovr")
	implementation("org.lwjgl:lwjgl-par")
	implementation("org.lwjgl:lwjgl-remotery")
	implementation("org.lwjgl:lwjgl-rpmalloc")
	implementation("org.lwjgl:lwjgl-shaderc")
	implementation("org.lwjgl:lwjgl-sse")
	implementation("org.lwjgl:lwjgl-stb")
	implementation("org.lwjgl:lwjgl-tinyexr")
	implementation("org.lwjgl:lwjgl-tinyfd")
	implementation("org.lwjgl:lwjgl-tootle")
	implementation("org.lwjgl:lwjgl-vma")
	implementation("org.lwjgl:lwjgl-vulkan")
	implementation("org.lwjgl:lwjgl-xxhash")
	implementation("org.lwjgl:lwjgl-yoga")
	implementation("org.lwjgl:lwjgl-zstd")
	runtimeOnly("org.lwjgl:lwjgl::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-assimp::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-bgfx::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-glfw::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-jemalloc::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-libdivide::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-llvm::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-lmdb::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-lz4::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-meow::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-nanovg::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-nfd::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-nuklear::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-openal::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-opengl::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-opengles::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-openvr::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-opus::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-ovr::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-par::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-remotery::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-rpmalloc::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-shaderc::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-sse::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-stb::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-tinyexr::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-tinyfd::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-tootle::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-vma::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-xxhash::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-yoga::$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-zstd::$lwjglNatives")
}

tasks {
	test {
		useJUnit()
	}
	jar {
		manifest {
			attributes["Main-Class"] = "dev.reeve.organization.mainKt"
		}
	}
}

tasks.withType<KotlinCompile>() {
	kotlinOptions.jvmTarget = "11"
}