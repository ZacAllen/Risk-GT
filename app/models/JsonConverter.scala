package models

import scala.collection.mutable.ListBuffer


object JsonConverter {
  def toJson(o: Any) : String = {
    var json = new ListBuffer[String]()
    var retCheck: Boolean = true
    var retString: String = ""
    o match {
      case m: Map[_,_] => {
        for ( (k,v) <- m ) {
          var key = escape(k.toString)
          v match {
            case a: Map[_,_] => json += "\"" + key + "\":" + toJson(a)
            case a: List[_] => json += "\"" + key + "\":" + toJson(a)
            case a: Int => json += "\"" + key + "\":" + a
            case a: Boolean => json += "\"" + key + "\":" + a
            case a: String => json += "\"" + key + "\":\"" + escape(a) + "\""
            case _ => ;
          }
        }
      }
      case m: List[_] => {
        var list = new ListBuffer[String]()
        for ( el <- m ) {
          el match {
            case a: Map[_,_] => list += toJson(a)
            case a: List[_] => list += toJson(a)
            case a: Int => list += a.toString()
            case a: Boolean => list += a.toString()
            case a: String => list += "\"" + escape(a) + "\""
            case _ => ;
          }
        }
        retString = "[" + list.mkString(",") + "]"
        retCheck = false
      }
      case _ => ;
    }
    if (retCheck) {
      retString = "{" + json.mkString(",") + "}"
    }
    retString
  }

  private def escape(s: String) : String = {
    s.replaceAll("\"" , "\\\\\"");
  }
}
