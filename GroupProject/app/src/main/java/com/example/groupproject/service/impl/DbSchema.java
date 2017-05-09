package com.example.groupproject.service.impl;

/**
 * Created by hasit on 5/4/2017.
 */

public class DbSchema {

    public static final class ZoneTable{
        public static final String NAME = "NAME";

        public static final class Columns{
            public static final
                    String ID = "ID",
                    ZONE_NAME = "ZONE_NAME",
                    ZONE_TYPE = "ZONE_TYPE",
                    START_LAT = "START_LAT",
                    START_LONG = "START_LONG",
                    END_LAT = "END_LAT",
                    END_LONG = "END_LONG";
        }
    }
}
