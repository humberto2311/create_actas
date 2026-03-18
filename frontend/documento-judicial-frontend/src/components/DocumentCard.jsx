import React from 'react';
import {
  Paper,
  Box,
  Typography,
  IconButton,
  Chip,
  Tooltip,
  CircularProgress
} from '@mui/material';
import {
  Edit as EditIcon,
  Delete as DeleteIcon,
  Description as DescriptionIcon,
  Visibility as ViewIcon
} from '@mui/icons-material';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';

const DocumentCard = ({ document, onEdit, onDelete, onGenerate, onView, isDeleting }) => {
  return (
    <Paper
      elevation={2}
      sx={{
        p: 2,
        mb: 2,
        borderLeft: 6,
        borderLeftColor: document.state ? 'success.main' : 'error.main',
        opacity: isDeleting ? 0.6 : 1,
        transition: 'opacity 0.2s'
      }}
    >
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <Box sx={{ flex: 1 }}>
          <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
            {document.names} {document.lastNames}
          </Typography>
          
          <Box sx={{ display: 'flex', gap: 2, mt: 1, flexWrap: 'wrap' }}>
            <Chip label={`ID: ${document.identity}`} size="small" variant="outlined" />
            {document.radicado && (
              <Chip label={`Radicado: ${document.radicado}`} size="small" color="primary" variant="outlined" />
            )}
            <Chip 
              label={document.state ? 'Activo' : 'Inactivo'}
              size="small"
              color={document.state ? 'success' : 'error'}
            />
          </Box>

          <Box sx={{ mt: 1 }}>
            <Typography variant="caption" color="textSecondary">
              📅 {format(new Date(document.date), 'PPP', { locale: es })}
            </Typography>
          </Box>
        </Box>

        <Box sx={{ display: 'flex', gap: 1 }}>
          <Tooltip title="Ver detalles">
            <IconButton size="small" color="info" onClick={() => onView(document)}>
              <ViewIcon />
            </IconButton>
          </Tooltip>

          <Tooltip title="Editar">
            <IconButton size="small" color="primary" onClick={() => onEdit(document)}>
              <EditIcon />
            </IconButton>
          </Tooltip>

          <Tooltip title="Generar documento">
            <IconButton size="small" color="secondary" onClick={() => onGenerate(document.id)}>
              <DescriptionIcon />
            </IconButton>
          </Tooltip>

          <Tooltip title="Eliminar">
            <IconButton 
              size="small" 
              color="error" 
              onClick={() => onDelete(document.id)}
              disabled={isDeleting}
            >
              {isDeleting ? <CircularProgress size={20} /> : <DeleteIcon />}
            </IconButton>
          </Tooltip>
        </Box>
      </Box>
    </Paper>
  );
};

export default DocumentCard;