import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object Connect {

    @JvmStatic
    fun main() {
        val conn: Connection? = connect()
        //addFolderquery(conn)
        //testFolder(conn)
        //addNotequery(conn)
       // testNote(conn)
        close(conn)
    }

    fun addFolder(id : Int, name : String, numNotes: Int) {
        val conn: Connection? = connect()
        addFolderquery(conn,id, name, numNotes)
        testFolder(conn)
        close(conn)
    }

    fun addNote(id : Int, name : String, folder : Int) {
        val conn: Connection? = connect()
        addNotequery(conn, id, name, folder)
        testNote(conn)
        close(conn)

    }

    fun connect(): Connection? {
        var conn: Connection? = null
        try {
            val url = "jdbc:sqlite:/Users/andrewzaki/Documents/Dev/cs398-project/project/notetaker.db"
            conn = DriverManager.getConnection(url)
            println("Connection to SQLite has been established.")
        } catch (e: SQLException) {
            println(e.message)
        }
        return conn
    }

    fun testFolder(conn: Connection?) {
        try {
            if (conn != null) {
                val sql = "select * from folders"
                val query = conn.createStatement()
                val results = query.executeQuery(sql)
                while (results.next()) {
                    val folderID = results.getInt("folderID")
                    val name = results.getString("name")
                    val numNotes = results.getInt("numNotes")
                    println(folderID.toString() + "\t" + name + "\t" + numNotes)
                }
            }
        }
        catch (ex: SQLException) {
            println(ex.message)
        }
    }

    fun testNote(conn: Connection?) {
        try {
            if (conn != null) {
                val sql = "select * from notes"
                val query = conn.createStatement()
                val results = query.executeQuery(sql)
                while (results.next()) {
                    val folderID = results.getInt("noteID")
                    val name = results.getString("name")
                    val numNotes = results.getInt("folderID")
                    println(folderID.toString() + "\t" + name + "\t" + numNotes)
                }
            }
        }
        catch (ex: SQLException) {
            println(ex.message)
        }
    }

    fun addFolderquery(conn:Connection?, id : Int, name : String, numNotes: Int) {
        try {
            if (conn != null) {
                val sql = "insert into folders values (${id}, '${name}', ${numNotes});"
                val query = conn.createStatement()
                query.executeQuery(sql)
            }
        }
        catch (ex: SQLException) {
            println(ex.message)
        }
    }

    fun addNotequery(conn:Connection?, id : Int, name : String, folder : Int) {
        try {
            if (conn != null) {
                val sql = "insert into notes values (${id}, '${name}', ${folder});"
                val query = conn.createStatement()
                query.executeQuery(sql)
            }
        }
        catch (ex: SQLException) {
            println(ex.message)
        }
    }

    fun close(conn:Connection?) {
        try {
            if (conn != null) {
                conn.close()
                println("Connection closed.")
            }
        } catch (ex: SQLException) {
            println(ex.message)
        }
    }
}