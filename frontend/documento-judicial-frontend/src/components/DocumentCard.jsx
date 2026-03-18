import React from 'react';
import {
  Paper,
  Box,
  Typography,
  IconButton,
  Chip,
  Tooltip
} from '@mui/material';
import {
  Edit as EditIcon,
  Delete as DeleteIcon,
  Description as DescriptionIcon,
  Visibility as ViewIcon
} from '@mui/icons-material';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';

const DocumentCard = ({ document, onEdit, onDelete, onGenerate, onView }) => {
  const getStatusColor = (state) => {
    return state ? 'success' : 'error';
  };

  const getStatusText = (state) => {
    return state ? 'Activo' : 'Inactivo';
  };

  return (
    <Paper
    component="div" 
      elevation={2}
      sx={{
        p: 2,
        mb: 2,
        transition: 'transform 0.2s, box-shadow 0.2s',
        '&:hover': {
          transform: 'translateY(-2px)',
          boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
        },
        position: 'relative',
        borderLeft: 6,
        borderLeftColor: document.state ? 'success.main' : 'error.main',
      }}
    >
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <Box sx={{ flex: 1 }}>
          <Typography variant="subtitle1" sx={{ fontWeight: 'bold', color: 'primary.main' }}>
            {document.names} {document.lastNames}
          </Typography>
          
          <Box sx={{ display: 'flex', gap: 2, mt: 1, flexWrap: 'wrap' }}>
            <Chip
              label={`ID: ${document.identity}`}
              size="small"
              variant="outlined"
            />
            {document.radicado && (
              <Chip
                label={`Radicado: ${document.radicado}`}
                size="small"
                color="primary"
                variant="outlined"
              />
            )}
            <Chip
              label={getStatusText(document.state)}
              size="small"
              color={getStatusColor(document.state)}
            />
          </Box>

          <Box sx={{ mt: 1, display: 'flex', gap: 3, flexWrap: 'wrap' }}>
            <Typography variant="caption" color="textSecondary">
              📅 {format(new Date(document.date), 'PPP', { locale: es })}
            </Typography>
            {document.typeAudience && (
              <Typography variant="caption" color="textSecondary">
                ⚖️ {document.typeAudience}
              </Typography>
            )}
            {document.fiscal && (
              <Typography variant="caption" color="textSecondary">
                👤 Fiscal: {document.fiscal}
              </Typography>
            )}
          </Box>

          {document.conduct && (
            <Typography variant="body2" sx={{ mt: 1, color: 'text.secondary' }}>
              <strong>Conducta:</strong> {document.conduct}
            </Typography>
          )}
        </Box>

        <Box sx={{ display: 'flex', gap: 1 }}>
          <Tooltip title="Ver detalles">
          
<IconButton 
  size="small" 
  color="info" 
  type="button"
  onClick={(e) => {
    e.preventDefault(); // ¡PREVENIR COMPORTAMIENTO POR DEFECTO!
    e.stopPropagation(); // Detener propagación
    onView(document);
  }}
>
  <ViewIcon />
</IconButton>

<Tooltip title="Editar">
  <IconButton 
    size="small" 
    color="primary" 
    onClick={(e) => {
      e.preventDefault();
      e.stopPropagation();
      onEdit(document);
    }}
  >
    <EditIcon />
  </IconButton>
</Tooltip>

<Tooltip title="Generar documento">
  <IconButton 
    size="small" 
    color="secondary" 
    onClick={(e) => {
      e.preventDefault();
      e.stopPropagation();
      onGenerate(document.id);
    }}
  >
    <DescriptionIcon />
  </IconButton>
</Tooltip>

<Tooltip title="Eliminar">
  <IconButton 
    size="small" 
    color="error" 
    onClick={(e) => {
      e.preventDefault();
      e.stopPropagation();
      onDelete(document.id);
    }}
  >
    <DeleteIcon />
  </IconButton>
</Tooltip>
          </Tooltip>
        </Box>
      </Box>
    </Paper>
  );
};

export default DocumentCard;