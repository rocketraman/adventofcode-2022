package day07

import Day
import Lines

class Day7 : Day() {
  override fun part1(input: Lines): Any {
    return populateRoot(input)
      .walk()
      .filter { it.size() <= 100_000 }
      .sumOf { it.size() }
  }

  override fun part2(input: Lines): Any {
    val rootDirectory = populateRoot(input)
    val total = 70_000_000
    val needed = 30_000_000
    val currentUsed = rootDirectory.size()
    val currentFree = total - currentUsed

    val smallest = rootDirectory.walk()
      .filter { it.size() + currentFree >= needed }
      .minByOrNull { it.size() }!!

    return smallest.size()
  }

  private fun populateRoot(input: Lines): Dir {
    val rootDirectory = Dir("/", mutableListOf(), null)
    var cwd = rootDirectory

    fun handleDir(dir: String) {
      // have we already seen it?
      if (cwd.inodes.any { it is Dir && it.name == dir }) return
      cwd.inodes.add(Dir(dir, mutableListOf(), cwd))
    }

    fun handleFile(file: File) {
      // have we already seen it?
      if (cwd.inodes.any { it is File && it == file }) return
      cwd.inodes.add(file)
    }

    fun handleCommand(cmdLine: String) {
      val (cmd, args) = cmdLine.split(" ").let { it[0] to it.getOrNull(1) }
      when (cmd) {
        "ls" -> return
        "cd" -> {
          cwd = when (args) {
            ".." -> cwd.parent ?: error("Invalid navigation from root dir")
            else -> cwd.inodes.filterIsInstance<Dir>().first { it.name == args }
          }
        }
      }
    }

    input.drop(2).forEach { l ->
      when {
        l.startsWith("$ ") -> handleCommand(l.substring(2))
        l.startsWith("dir ") -> handleDir(l.substring(4))
        else -> handleFile(l.split(" ").let { (size, name) -> File(name, size.toInt()) })
      }
    }

    return rootDirectory
  }

  sealed class Inode
  data class File(val name: String, val size: Int): Inode()
  data class Dir(val name: String, val inodes: MutableList<Inode>, val parent: Dir?): Inode() {
    fun size(): Int = inodes.sumOf { i ->
      when (i) {
        is Dir -> i.size()
        is File -> i.size
      }
    }
    fun walk(): List<Dir> = inodes.filterIsInstance<Dir>().flatMap { it.walk() } + this
  }
}
