package models


class Player(id: Int, name: String, email: String, var turn: Int, color: String, var numArmies: Int, territories: List[Territory]) {

  def getName: String = this.name

  def getId: Int = this.id

  def getEmail: String = this.email

  def getTurn: Int = this.turn

  def getColor: String = this.color

  def getNumArmies: Int = this.numArmies

  def addArmyUnits(num: Int): Int = {
    this.numArmies += num
    numArmies
  }

  def getTerritories: List[Territory] = this.territories

  def setTurn(turn: Int){this.turn = turn}
}

object Player {

  def apply(id: Int, name: String, email: String, turn: Int, color: String, numArmies: Int, territories: List[Territory]):
    Player = new Player(id, name, email, turn, color, numArmies, territories)
}
