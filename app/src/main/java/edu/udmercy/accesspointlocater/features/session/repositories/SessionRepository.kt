package edu.udmercy.accesspointlocater.features.session.repositories

interface SessionRepository {
    fun createNewSession(timestamp: String, sessionLabel: String, buildingName: String, path: String)
}