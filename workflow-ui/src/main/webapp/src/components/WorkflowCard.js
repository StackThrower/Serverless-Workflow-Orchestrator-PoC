import React from 'react';
import { Link } from 'react-router-dom';

const WorkflowCard = ({ workflow }) => {
  const getStatusColor = (status) => {
    switch (status?.toLowerCase()) {
      case 'pending':
        return 'bg-yellow-100 text-yellow-800';
      case 'running':
        return 'bg-blue-100 text-blue-800';
      case 'completed':
        return 'bg-green-100 text-green-800';
      case 'failed':
        return 'bg-red-100 text-red-800';
      case 'cancelled':
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleString();
  };

  const getDuration = (startedAt, completedAt) => {
    if (!startedAt) return 'N/A';

    const start = new Date(startedAt);
    const end = completedAt ? new Date(completedAt) : new Date();
    const durationMs = end - start;

    const seconds = Math.floor(durationMs / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);

    if (hours > 0) {
      return `${hours}h ${minutes % 60}m ${seconds % 60}s`;
    } else if (minutes > 0) {
      return `${minutes}m ${seconds % 60}s`;
    } else {
      return `${seconds}s`;
    }
  };

  return (
    <Link
      to={`/workflow/${workflow.id}`}
      className="block bg-white rounded-lg shadow-sm border border-gray-200 hover:shadow-md transition-shadow"
    >
      <div className="p-6">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center space-x-3">
            <div className="flex-shrink-0">
              <div className="w-10 h-10 bg-gray-100 rounded-lg flex items-center justify-center">
                <span className="text-gray-600 font-medium text-sm">
                  {workflow.id?.substring(0, 2).toUpperCase()}
                </span>
              </div>
            </div>
            <div>
              <h3 className="text-lg font-semibold text-gray-900">
                Workflow {workflow.id?.substring(0, 8)}
              </h3>
              <p className="text-sm text-gray-500">
                {workflow.currentStepId ? `Current: ${workflow.currentStepId}` : 'Not started'}
              </p>
            </div>
          </div>

          <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(workflow.status)}`}>
            {workflow.status}
          </span>
        </div>

        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
          <div>
            <p className="text-gray-500 font-medium">Started</p>
            <p className="text-gray-900">{formatDate(workflow.startedAt)}</p>
          </div>

          <div>
            <p className="text-gray-500 font-medium">Duration</p>
            <p className="text-gray-900">{getDuration(workflow.startedAt, workflow.completedAt)}</p>
          </div>

          <div>
            <p className="text-gray-500 font-medium">Updated</p>
            <p className="text-gray-900">{formatDate(workflow.updatedAt)}</p>
          </div>

          <div>
            <p className="text-gray-500 font-medium">Definition ID</p>
            <p className="text-gray-900 font-mono text-xs">
              {workflow.workflowDefinitionId?.substring(0, 8)}...
            </p>
          </div>
        </div>

        {workflow.errorMessage && (
          <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-md">
            <p className="text-sm text-red-700 font-medium">Error:</p>
            <p className="text-sm text-red-600 mt-1">{workflow.errorMessage}</p>
          </div>
        )}
      </div>
    </Link>
  );
};

export default WorkflowCard;
