import java.io.*
import java.util.TreeMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.math.pow

fun main() {
    val n = 40
    val interval = listOf(0.0, 5.0)
    val grid = makeGrid(interval, n)
    val h = (interval.last() - interval.first()) / n
    val values = ConcurrentHashMap<Double, Double>()

    val threadPool = Executors.newFixedThreadPool(10)
    val tasks = ArrayList<Future<*>>()
    for (i in 0..n) {
        val future = threadPool.submit {
            val x = grid[i]
            values[x] = (1 - h).pow(i)
        }

        tasks.add(future)
    }

    tasks.forEach { e ->
        e.get()
    }

    threadPool.shutdown()

    val treeValues = TreeMap(values)
    val xs = ArrayList(treeValues.keys)
    val ys = ArrayList(treeValues.values)

    createGraphics(xs, ys)
}

fun makeGrid(interval: List<Double>, intervalsCnt: Int): List<Double> {
    val points = ArrayList<Double>()

    val startPoint = interval.first()
    val length = interval.last() - interval.first()
    var cnt = 1
    points.add(interval.first())

     do {
        val nextPoint = startPoint + length * cnt / (intervalsCnt)
        points.add(nextPoint)
        cnt += 1
    } while (nextPoint != interval.last())

    return points
}

fun createGraphics(xs: List<Double>, ys: List<Double>) {
    val templateReader = DataInputStream(FileInputStream("src/main/resources/template.html"))
    val html = File("src/main/resources/graphics.html")
    html.createNewFile()

    val htmlWriter = DataOutputStream(FileOutputStream(html))
    val htmlStr = String(templateReader.readAllBytes())
    val newStr = htmlStr.replace("%x", xs.toString()).replace("%y", ys.toString())
    htmlWriter.write(newStr.toByteArray())
}