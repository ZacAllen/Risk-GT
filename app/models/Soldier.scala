package models

abstract class Soldier(id: Int, name: String, var territory: Territory, price: Int, mobility: Int, var canMove: Boolean) extends Ordered[Soldier] {

  def compare(that: Soldier): Int = this.price - that.getPrice

  def getName: String = this.name

  def getId: Int = this.id

  def getLocation: Territory = this.territory

  def getPrice: Int = this.price

  def getMobility: Int = this.mobility

  def getCanMove: Boolean = this.canMove

  def setCanMove(boolean: Boolean){this.canMove = boolean}

  def setTerritory(territory: Territory) {this.territory = territory}
}



case class Student(id: Int, var territoryS: Territory) extends Soldier(id, "Student", territoryS,
  1, 1, true) {
}

case class  TA(id: Int, var territoryT: Territory) extends Soldier(id, "TA", territoryT,
  2, 2, true) {
}

case class Professor(id: Int, territoryP: Territory) extends Soldier(id, "Professor", territoryP,
  3, 1, true) {
}
