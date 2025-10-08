import React from 'react';

const StatusFilter = ({ selectedStatus, onStatusChange }) => {
  const statuses = [
    { value: 'all', label: 'All', count: 0 },
    { value: 'pending', label: 'Pending', count: 0 },
    { value: 'running', label: 'Running', count: 0 },
    { value: 'completed', label: 'Completed', count: 0 },
    { value: 'failed', label: 'Failed', count: 0 }
  ];

  return (
    <div className="flex space-x-1 bg-gray-100 rounded-lg p-1">
      {statuses.map((status) => (
        <button
          key={status.value}
          onClick={() => onStatusChange(status.value)}
          className={`px-3 py-1.5 text-sm font-medium rounded-md transition-colors ${
            selectedStatus === status.value
              ? 'bg-white text-gray-900 shadow-sm'
              : 'text-gray-500 hover:text-gray-700'
          }`}
        >
          {status.label}
        </button>
      ))}
    </div>
  );
};

export default StatusFilter;
