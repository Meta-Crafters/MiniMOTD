plugins {
  id("minimotd.platform-conventions")
  id("com.github.johnrengelman.shadow")
  id("quiet-fabric-loom")
}

val shade: Configuration by configurations.creating

val minecraftVersion = libs.versions.fabricMinecraft.get()

dependencies {
  minecraft(libs.fabricMinecraft)
  mappings(loom.officialMojangMappings())
  modImplementation(libs.fabricLoader)
  modImplementation(libs.fabricApi)

  shade(implementation(projects.minimotdCommon) {
    exclude("net.kyori")
  })

  modImplementation(libs.adventurePlatformFabric)
  include(libs.adventurePlatformFabric)
}

miniMOTDPlatform {
  jarTask.set(tasks.remapJar)
}

indra {
  javaVersions {
    target(17)
  }
}

loom {
  serverOnlyMinecraftJar()
}

tasks {
  shadowJar {
    configurations = listOf(shade)
    commonConfiguration()
    commonRelocation("io.leangen.geantyref")
    platformRelocation("fabric", "xyz.jpenilla.minimotd.common")
  }
  remapJar {
    archiveFileName.set("${project.name}-mc$minecraftVersion-${project.version}.jar")
  }
  processResources {
    val replacements = mapOf(
      "modid" to project.name,
      "name" to rootProject.name,
      "version" to project.version.toString(),
      "description" to project.description.toString(),
      "github_url" to Constants.GITHUB_URL
    )
    inputs.properties(replacements)
    filesMatching("fabric.mod.json") {
      expand(replacements)
    }
  }
}
