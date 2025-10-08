import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import WorkflowCard from './WorkflowCard';
import StatusFilter from './StatusFilter';
import StartWorkflowModal from './StartWorkflowModal';

const Dashboard = () => {
  const [workflows, setWorkflows] = useState([]);
  const [filteredWorkflows, setFilteredWorkflows] = useState([]);
  const [selectedStatus, setSelectedStatus] = useState('all');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showStartModal, setShowStartModal] = useState(false);
  const [stats, setStats] = useState({
    total: 0,
    running: 0,
    completed: 0,
    failed: 0
  });

  useEffect(() => {
    fetchWorkflows();
    const interval = setInterval(fetchWorkflows, 5000); // Refresh every 5 seconds
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    filterWorkflows();
    calculateStats();
  }, [workflows, selectedStatus]);

  const fetchWorkflows = async () => {
    try {
      const response = await axios.get('/api/workflows');
      setWorkflows(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch workflows');
      console.error('Error fetching workflows:', err);
    } finally {
      setLoading(false);
    }
  };

  const filterWorkflows = () => {
    if (selectedStatus === 'all') {
      setFilteredWorkflows(workflows);
    } else {
      setFilteredWorkflows(workflows.filter(w => w.status.toLowerCase() === selectedStatus));
    }
  };

  const calculateStats = () => {
    const stats = workflows.reduce((acc, workflow) => {
      acc.total++;
      const status = workflow.status.toLowerCase();
      if (status === 'running') acc.running++;
      else if (status === 'completed') acc.completed++;
      else if (status === 'failed') acc.failed++;
      return acc;
    }, { total: 0, running: 0, completed: 0, failed: 0 });

    setStats(stats);
  };

  const handleWorkflowStarted = () => {
    fetchWorkflows();
    setShowStartModal(false);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
                <span className="text-white text-sm font-medium">{stats.total}</span>
              </div>
            </div>
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-500">Total</p>
              <p className="text-lg font-semibold text-gray-900">{stats.total}</p>
            </div>
          </div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-yellow-500 rounded-full flex items-center justify-center">
                <span className="text-white text-sm font-medium">{stats.running}</span>
              </div>
            </div>
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-500">Running</p>
              <p className="text-lg font-semibold text-gray-900">{stats.running}</p>
            </div>
          </div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-green-500 rounded-full flex items-center justify-center">
                <span className="text-white text-sm font-medium">{stats.completed}</span>
              </div>
            </div>
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-500">Completed</p>
              <p className="text-lg font-semibold text-gray-900">{stats.completed}</p>
            </div>
          </div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-red-500 rounded-full flex items-center justify-center">
                <span className="text-white text-sm font-medium">{stats.failed}</span>
              </div>
            </div>
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-500">Failed</p>
              <p className="text-lg font-semibold text-gray-900">{stats.failed}</p>
            </div>
          </div>
        </div>
      </div>

      {/* Controls */}
      <div className="flex justify-between items-center">
        <div className="flex items-center space-x-4">
          <h2 className="text-2xl font-bold text-gray-900">Workflows</h2>
          <StatusFilter
            selectedStatus={selectedStatus}
            onStatusChange={setSelectedStatus}
          />
        </div>

        <button
          onClick={() => setShowStartModal(true)}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
        >
          Start Workflow
        </button>
      </div>

      {/* Error Message */}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      {/* Workflows List */}
      <div className="grid gap-4">
        {filteredWorkflows.length === 0 ? (
          <div className="text-center py-12">
            <div className="text-gray-500 text-lg">No workflows found</div>
            <p className="text-gray-400 mt-2">
              {selectedStatus === 'all'
                ? 'Start your first workflow to get started'
                : `No ${selectedStatus} workflows`
              }
            </p>
          </div>
        ) : (
          filteredWorkflows.map((workflow) => (
            <WorkflowCard key={workflow.id} workflow={workflow} />
          ))
        )}
      </div>

      {/* Start Workflow Modal */}
      {showStartModal && (
        <StartWorkflowModal
          onClose={() => setShowStartModal(false)}
          onWorkflowStarted={handleWorkflowStarted}
        />
      )}
    </div>
  );
};

export default Dashboard;
