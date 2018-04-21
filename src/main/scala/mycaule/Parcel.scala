package mycaule

// import com.typesafe.scalalogging._
//
// import org.apache.spark.{ SparkContext, SparkConf }
// import org.apache.spark.rdd.RDD

object Parcel extends App {

  object Validation {
    def check[A](l: List[A], p: A => Boolean): Boolean = {
      val i = l.indexWhere(p)

      if (i < 0)
        true
      else {
        println(s"[warn] Incorrect value ${l(i)} at position $i.")
        false
      }
    }

    def in(items: List[Int]): Boolean =
      check[Int](items, x => x < 1 || 9 < x)

    def out(parcels: List[List[Int]], items: List[Int]): Boolean =
      check[List[Int]](parcels, x => {
        val sum = x.sum
        sum < 0 || 10 < sum
      }) &&
        (parcels.foldLeft(List[Int]())((acc, elt) => acc ++ elt).groupBy(identity).map(t => (t._1, t._2.size)).toSet
          diff items.groupBy(identity).map(t => (t._1, t._2.size)).toSet).isEmpty
  }

  object Serialization {
    def readIn(str: String): List[Int] = str.toList.map(_.asDigit)

    def readOut(str: String): List[List[Int]] = str.split("/").toList.map(readIn)

    def writeIn(items: List[Int]): String = items.mkString("")

    def writeOut(parcels: List[List[Int]]): String = parcels.map(writeIn).mkString("/")
  }

  def partition(items: List[Int], capacity: Int): List[(List[Int], Int)] = {
    items.foldLeft(Array((List[Int](), 0)))((acc, item) => {
      val l = acc.last
      val sum = l._2 + item
      if (sum <= capacity) {
        acc(acc.length - 1) = (l._1 :+ item, sum)
        acc
      } else {
        acc :+ (List(item), item)
      }
    }).toList
  }

  def montecarlo(items: List[Int], capacity: Int, draws: Int): List[(List[Int], Int)] = {
    import util.Random._

    val n = items.size
    println(f"[info] Monte Carlo pour n=$n, tirages=$draws, permutations=${math.sqrt(2 * math.Pi * n) * math.pow(n / math.E, n) * (1 + 1 / (12 * n))}%.3E")
    (1 to draws).toList.foldLeft(partition(items, capacity))((acc, item) => {
      val d = partition(shuffle(items), capacity)
      if (d.size < acc.size) d else acc
    })
  }

  override def main(args: Array[String]): Unit = {
    val MAX_CAPACITY = 10
    val MAX_ITERATIONS = 10000

    println("Chaîne d'articles en entrée :")
    val str: String = Console.readLine
    val items = Serialization.readIn(str)

    val dataValid = Validation.in(items)

    if (dataValid) {
      println(s"${items.size} articles à emballer")
      val algorithms = List((partition(items, MAX_CAPACITY), "temps réel"), (montecarlo(items, MAX_CAPACITY, MAX_ITERATIONS), "montecarlo"))

      println("Chaîne d'articles emballés :")
      algorithms map {
        case (parcels, name) => {
          val results = parcels.map(_._1)
          val contents = parcels.map(_._2)
          val resultsValid = Validation.out(results, items)
          if (resultsValid) {
            val compacity = contents.sum.toDouble / contents.size / MAX_CAPACITY
            println(f"$name - ${Serialization.writeOut(results)} (${parcels.size} cartons, K ~ $compacity%1.2f)")
          } else {
            println("[warn] Certains cartons sont incorrects !")
          }
        }
      }
    } else {
      println("[warn] Certains articles sont incorrects !")
    }
  }
}
