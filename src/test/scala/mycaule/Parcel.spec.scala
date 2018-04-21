package mycaule

import org.scalatest._

class ParcelSpec extends FlatSpec with Matchers {
  val items = List(1, 6, 3, 8, 4, 1, 6, 8, 9, 5, 2, 5, 7, 7, 3)

  "The Parcel object" should "validate the list of items" in {
    Parcel.Validation.in(items) shouldBe true
    Parcel.Validation.in(0 :: items) shouldBe false
    Parcel.Validation.in(10 :: items) shouldBe false
  }

  it should "validate the list of parcels" in {
    val parcels = items.map(x => List(x))
    Parcel.Validation.out(parcels, items) shouldBe true
    Parcel.Validation.out(List(2, 9) :: parcels, items) shouldBe false
    Parcel.Validation.out(List(12, 1) :: parcels, items) shouldBe false
    Parcel.Validation.out(List(1, 1) :: parcels, items) shouldBe false
  }

  it should "serialize the list of items" in {
    Parcel.Serialization.writeIn(items) shouldBe "163841689525773"
    Parcel.Serialization.readIn("163841689525773") shouldEqual items
  }

  it should "serialize the list of parcels" in {
    val parcels = items.map(x => List(x))
    Parcel.Serialization.writeOut(parcels) shouldBe "1/6/3/8/4/1/6/8/9/5/2/5/7/7/3"
    Parcel.Serialization.readOut("1/6/3/8/4/1/6/8/9/5/2/5/7/7/3") shouldEqual parcels
  }

  it should "group items with naive approach" in {
    val parcels = Parcel.partition(items, 10)
    parcels.size shouldEqual 10
    Parcel.Serialization.writeOut(parcels.map(_._1)) shouldBe "163/8/41/6/8/9/52/5/7/73"
  }

  it should "group items with another approach" in {
    val parcels = Parcel.montecarlo(items, 10, 1000)
    parcels.size shouldEqual 8
  }
}
