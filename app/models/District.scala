package models

// Like continents in the original Risk game.
class District(id: Int, name: String, territories: List[Territory]) {
  def getId: Int = id

  def getName: String = name

  def getTerritories: List[Territory] = territories
}

object District {
  def apply(id: Int, name: String, territories: List[Territory]): District = new District(id, name, territories)
}
