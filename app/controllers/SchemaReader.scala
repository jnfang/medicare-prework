package controllers


import scala.io.Source

object SchemaReader {
  sealed trait DataType { def name: String}
  case object Text extends DataType {val name = "TEXT"}
  case object Integer extends DataType {val name = "INTEGER"}
  case object Boolean extends DataType { val name = "BOOLEAN"}

  case class SchemaColumn(name:String, index: Int, width: Int, dataType: DataType)

  def readFromFile(fileName: String) : List[SchemaColumn] = {
    val schemaFile = Source.fromFile(fileName)
    val lines = schemaFile.getLines.toList
    schemaFile.close
    val linesWithIndices = computeIndices(lines)
    linesWithIndices.map {
      case (line, startIndex) =>
        val column = line.split(",").toList
        if (column.length == 3) {
          val columnName = column(0)
          val columnWidth = column(1).toInt
          val columnDataType = column(2).toString match {
            case Text.name => Text
            case Integer.name => Integer
            case Boolean.name => Boolean
          }
          SchemaColumn(name = columnName, index = startIndex, width = columnWidth, dataType = columnDataType)
        }
        else
        {
          throw new IllegalStateException("Too many columns")
        }
    }
  }

  def computeIndices(lines: List[String]): List[(String, Int)] = {
    val indices = lines.map {
      line => line.split(",").toList(1).toInt
    }
    val cumulativeIndices = indices.foldLeft(List.empty[Int]){
      case (accum, next) => accum :+ next + accum.lastOption.getOrElse(0)
    }
    lines.zip(cumulativeIndices)
  }
}