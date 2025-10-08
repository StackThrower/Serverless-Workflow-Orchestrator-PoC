import React, { useState, useEffect } from 'react';
import axios from 'axios';

const StartWorkflowModal = ({ onClose, onWorkflowStarted }) => {
  const [workflowDefinitions, setWorkflowDefinitions] = useState([]);
  const [selectedWorkflow, setSelectedWorkflow] = useState('');
  const [inputData, setInputData] = useState('{"userId": 123, "action": "process"}');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchWorkflowDefinitions();
  }, []);

  const fetchWorkflowDefinitions = async () => {
    try {
      const response = await axios.get('/api/definitions');
      setWorkflowDefinitions(response.data);
      if (response.data.length > 0) {
        setSelectedWorkflow(response.data[0].name);
      }
    } catch (err) {
      setError('Failed to fetch workflow definitions');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedWorkflow) return;

    setLoading(true);
    setError('');

    try {
      await axios.post(`/api/workflows/start/${selectedWorkflow}`, {
        input: inputData
      });
      onWorkflowStarted();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to start workflow');
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <div className="mt-3">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-medium text-gray-900">Start New Workflow</h3>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600"
            >
              <span className="sr-only">Close</span>
              <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="mb-4">
              <label htmlFor="workflow" className="block text-sm font-medium text-gray-700 mb-2">
                Workflow Definition
              </label>
              <select
                id="workflow"
                value={selectedWorkflow}
                onChange={(e) => setSelectedWorkflow(e.target.value)}
                className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                required
              >
                <option value="">Select a workflow</option>
                {workflowDefinitions.map((def) => (
                  <option key={def.id} value={def.name}>
                    {def.name} (v{def.version})
                  </option>
                ))}
              </select>
            </div>

            <div className="mb-6">
              <label htmlFor="input" className="block text-sm font-medium text-gray-700 mb-2">
                Input Data (JSON)
              </label>
              <textarea
                id="input"
                value={inputData}
                onChange={(e) => setInputData(e.target.value)}
                rows={4}
                className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-blue-500 focus:border-blue-500 font-mono text-sm"
                placeholder='{"key": "value"}'
              />
            </div>

            {error && (
              <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-md">
                <p className="text-sm text-red-700">{error}</p>
              </div>
            )}

            <div className="flex space-x-3">
              <button
                type="button"
                onClick={onClose}
                className="flex-1 bg-gray-100 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-200 transition-colors"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={loading || !selectedWorkflow}
                className="flex-1 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 disabled:bg-blue-300 transition-colors"
              >
                {loading ? 'Starting...' : 'Start Workflow'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default StartWorkflowModal;
