package ru.quipy.taskManager.projects.api

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType(aggregateEventsTableName = "projects")
class ProjectAggregate: Aggregate