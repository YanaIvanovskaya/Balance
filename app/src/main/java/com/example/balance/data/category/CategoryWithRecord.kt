package com.example.balance.data

import androidx.room.Embedded
import androidx.room.Relation
import com.example.balance.data.record.Record

class CategoryWithRecords {

    @Embedded
    var category: Category? = null

    @Relation(parentColumn = "id", entityColumn = "category_id")
    var records: List<Record>? = null

}