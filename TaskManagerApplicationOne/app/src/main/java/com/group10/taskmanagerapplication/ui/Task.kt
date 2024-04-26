package com.group10.taskmanagerapplication.ui

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Task(
    val name: String = "",
    val description: String? = null,
    val startDate: Long = 0L, // Change type to Long
    val endDate: Long = 0L,   // Change type to Long
    var isCompleted: Boolean = false,
    val resources: List<ResourceItem>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte()
    )

    // No-argument constructor required for Firestore deserialization
    constructor() : this("", null, 0L, 0L, false)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeLong(startDate)
        parcel.writeLong(endDate)
        parcel.writeByte(if (isCompleted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }

        // Function to convert Firestore document data to Task object
        fun toTask(documentId: String, documentData: Map<String, Any>): Task {
            return Task(
                name = documentData["name"] as String? ?: "",
                description = documentData["description"] as String?,
                startDate = (documentData["startDate"] as Long?) ?: 0L, // Use Long instead of Timestamp
                endDate = (documentData["endDate"] as Long?) ?: 0L,     // Use Long instead of Timestamp
                isCompleted = documentData["isCompleted"] as Boolean? ?: false
            )
        }
    }
}
