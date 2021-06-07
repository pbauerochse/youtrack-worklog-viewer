package de.pbauerochse.worklogviewer.datasource.api.domain.adapters

import de.pbauerochse.worklogviewer.datasource.api.domain.YouTrackUser
import de.pbauerochse.worklogviewer.timereport.User

class UserAdapter(user: YouTrackUser): User(user.id, user.fullName)