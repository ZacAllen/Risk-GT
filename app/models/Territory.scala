package models

class Territory(id: Int, var ownerName: Any, var owner: Player, var ownerID: Any, var numUnits: Int, district: District, var canAttack: Boolean) {

  def getId: Int = this.id

  def getOwnerName: Any = this.ownerName

  def setOwnerName(name: Any) { this.ownerName = name}

  def getUnits: Int = this.numUnits

  //Adds units and returns new total # of units in the territory
  def addUnits(newUnits: Int): Int = {
    this.numUnits += newUnits
    numUnits
  }

  def getOwner: Player = this.owner

  def getDistrict: District = this.district

  def getCanAttack: Boolean = this.canAttack

  def setCanAttack(canAttack: Boolean) {this.canAttack = canAttack}

  def setOwner(player: Player) {this.owner = player}

  def setOwnerID(id : Any): Unit = this.ownerID = id

  def getOwnerID: Any = this.ownerID

  def armyChange(num: Int) {this.numUnits += num}
}
