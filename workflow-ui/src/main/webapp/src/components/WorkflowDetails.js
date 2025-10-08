import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from 'axios';

const WorkflowDetails = () => {
  const { id } = useParams();
  const [workflow, setWorkflow] = useState(null);
  const [steps, setSteps] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchWorkflowDetails();
    fetchWorkflowSteps();

    const interval = setInterval(() => {
      fetchWorkflowDetails();
      fetchWorkflowSteps();
    }, 3000); // Refresh every 3 seconds

    return () => clearInterval(interval);
  }, [id]);

  const fetchWorkflowDetails = async () => {
    try {
      const response = await axios.get(`/api/workflows/${id}`);
      setWorkflow(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch workflow details');
    }
  };

  const fetchWorkflowSteps = async () => {
    try {
      const response = await axios.get(`/api/workflows/${id}/steps`);
      setSteps(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch workflow steps');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status?.toLowerCase()) {
      case 'pending':
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'running':
        return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'completed':
        return 'bg-green-100 text-green-800 border-green-200';
      case 'failed':
        return 'bg-red-100 text-red-800 border-red-200';
      case 'skipped':
        return 'bg-gray-100 text-gray-800 border-gray-200';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-200';
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

    if (minutes > 0) {
      return `${minutes}m ${seconds % 60}s`;
    } else {
      return `${seconds}s`;
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
        {error}
      </div>
    );
  }

  if (!workflow) {
    return (
      <div className="text-center py-12">
        <div className="text-gray-500 text-lg">Workflow not found</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Breadcrumb */}
      <nav className="flex" aria-label="Breadcrumb">
        <ol className="flex items-center space-x-4">
          <li>
            <Link to="/" className="text-blue-600 hover:text-blue-800">
              Dashboard
            </Link>
          </li>
          <li>
            <span className="text-gray-400">/</span>
          </li>
          <li>
            <span className="text-gray-500">Workflow {workflow.id.substring(0, 8)}</span>
          </li>
        </ol>
      </nav>

      {/* Workflow Overview */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">
              Workflow Details
            </h1>
            <p className="text-gray-500 mt-1">ID: {workflow.id}</p>
          </div>

          <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium border ${getStatusColor(workflow.status)}`}>
            {workflow.status}
          </span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div>
            <p className="text-sm font-medium text-gray-500">Started At</p>
            <p className="text-lg text-gray-900">{formatDate(workflow.startedAt)}</p>
          </div>

          <div>
            <p className="text-sm font-medium text-gray-500">Duration</p>
            <p className="text-lg text-gray-900">{getDuration(workflow.startedAt, workflow.completedAt)}</p>
          </div>

          <div>
            <p className="text-sm font-medium text-gray-500">Current Step</p>
            <p className="text-lg text-gray-900">{workflow.currentStepId || 'N/A'}</p>
          </div>

          <div>
            <p className="text-sm font-medium text-gray-500">Last Updated</p>
            <p className="text-lg text-gray-900">{formatDate(workflow.updatedAt)}</p>
          </div>
        </div>

        {workflow.input && (
          <div className="mt-6">
            <p className="text-sm font-medium text-gray-500 mb-2">Input</p>
            <pre className="bg-gray-50 border border-gray-200 rounded-md p-3 text-sm overflow-x-auto">
              {JSON.stringify(JSON.parse(workflow.input), null, 2)}
            </pre>
          </div>
        )}

        {workflow.output && (
          <div className="mt-6">
            <p className="text-sm font-medium text-gray-500 mb-2">Output</p>
            <pre className="bg-gray-50 border border-gray-200 rounded-md p-3 text-sm overflow-x-auto">
              {JSON.stringify(JSON.parse(workflow.output), null, 2)}
            </pre>
          </div>
        )}

        {workflow.errorMessage && (
          <div className="mt-6">
            <p className="text-sm font-medium text-red-600 mb-2">Error</p>
            <div className="bg-red-50 border border-red-200 rounded-md p-3 text-sm text-red-700">
              {workflow.errorMessage}
            </div>
          </div>
        )}
      </div>

      {/* Workflow Steps */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200">
        <div className="px-6 py-4 border-b border-gray-200">
          <h2 className="text-lg font-semibold text-gray-900">
            Execution Steps ({steps.length})
          </h2>
        </div>

        <div className="p-6">
          {steps.length === 0 ? (
            <div className="text-center py-8 text-gray-500">
              No steps executed yet
            </div>
          ) : (
            <div className="space-y-4">
              {steps.map((step, index) => (
                <div key={step.id} className="flex items-start space-x-4">
                  <div className="flex-shrink-0 w-8 h-8 bg-gray-100 rounded-full flex items-center justify-center text-sm font-medium text-gray-600">
                    {index + 1}
                  </div>

                  <div className="flex-grow bg-gray-50 border border-gray-200 rounded-lg p-4">
                    <div className="flex items-center justify-between mb-2">
                      <div>
                        <h3 className="text-sm font-semibold text-gray-900">{step.stepId}</h3>
                        <p className="text-xs text-gray-500">{step.stepType}</p>
                      </div>

                      <div className="flex items-center space-x-2">
                        <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium border ${getStatusColor(step.status)}`}>
                          {step.status}
                        </span>
                        {step.retryCount > 0 && (
                          <span className="text-xs text-orange-600 bg-orange-100 px-2 py-1 rounded-full">
                            Retry {step.retryCount}
                          </span>
                        )}
                      </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4 text-xs text-gray-600 mb-2">
                      <div>
                        <span className="font-medium">Started:</span> {formatDate(step.startedAt)}
                      </div>
                      <div>
                        <span className="font-medium">Duration:</span> {getDuration(step.startedAt, step.completedAt)}
                      </div>
                    </div>

                    {step.output && (
                      <div className="mt-3">
                        <p className="text-xs font-medium text-gray-500 mb-1">Output</p>
                        <pre className="bg-white border border-gray-200 rounded p-2 text-xs overflow-x-auto max-h-32">
                          {step.output}
                        </pre>
                      </div>
                    )}

                    {step.errorMessage && (
                      <div className="mt-3">
                        <p className="text-xs font-medium text-red-600 mb-1">Error</p>
                        <div className="bg-red-50 border border-red-200 rounded p-2 text-xs text-red-700">
                          {step.errorMessage}
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default WorkflowDetails;
