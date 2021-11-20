<?php

class DB
{
    private $config = [
        'host' => 'localhost',
        'name' => 'kojast',
        'pass' => '',
        'user' => 'root',
    ];
    private $con;

    public function __construct()
    {
        $this->con = new MySQLi($this->config['host'], $this->config['user'], $this->config['pass'], $this->config['name']);
        if ($this->con->connect_errno) {
            exit('Error in database connection');
        }
        $this->con->set_charset('utf8');
    }

    public function affectedRows()
    {
        return $this->con->affected_rows;
    }

    public function arrayQuery($query)
    {
        $result = [];
        $rows = $this->con->query($query);
        if ($rows && $rows->num_rows > 0) {
            while ($row = $rows->fetch_object()) {
                $result[] = $row;
            }
            $rows->free();
        }
        return $result;
    }

    public function error()
    {
        return $this->con->error;
    }

    public function escape($value, $quote = true)
    {
        $value = $this->con->real_escape_string($value);
        if ($quote) {
            return "'{$value}'";
        }
        return $value;
    }

    public function insertId()
    {
        return $this->con->insert_id;
    }

    public function query($query)
    {
        return $this->con->query($query);
    }

    public function __destruct()
    {
        @$this->con->close();
    }
}