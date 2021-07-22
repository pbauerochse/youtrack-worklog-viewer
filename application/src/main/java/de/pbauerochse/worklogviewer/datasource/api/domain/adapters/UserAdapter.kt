package de.pbauerochse.worklogviewer.datasource.api.domain.adapters

import de.pbauerochse.worklogviewer.timereport.User

class UserAdapter(user: de.pbauerochse.worklogviewer.datasource.api.domain.User): User(user.id, user.fullName)